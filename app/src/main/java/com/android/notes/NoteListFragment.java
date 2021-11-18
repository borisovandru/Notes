package com.android.notes;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class NoteListFragment extends Fragment {

    private RecyclerView recyclerView;
    private Context context;
    private NoteSource data;
    private NoteAdapter adapter;
    private static final int MY_DEFAULT_DURATION = 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        context = getContext();
        initView(view);
        // создаем подключение и получаем данные. метод init ооповещает обозревателей, т.к метод
        // асинхронный
        data = new NoteSourceFirebaseImp().init(noteSource -> adapter.notifyDataSetChanged());
        adapter.setDataSource(data);
        initPopupMenu(view);
    }

    private void initView(View view) {
        TextView userName = view.findViewById(R.id.user_name);
        userName.setText(User.INSTANCE.getNameUser());
        recyclerView = view.findViewById(R.id.recycler_view_notes);
        initRecyclerView();
    }


    private void initPopupMenu(View view) {
        Button btnPopupMenu = view.findViewById(R.id.btn_popup_menu);
        btnPopupMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(context, v);
            popup.inflate(R.menu.popup_main_menu);
            popup.show();
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.action_add) {
                    //2.* Переделайте ввод и редактирование данных через диалоговое окно.
                    DialogFragment dlgBuilder = new NoteDialogCreateFragment(data.size());
                    MainActivity activity = (MainActivity) context;
                    Navigation navigation = activity.getNavigation();
                    dlgBuilder.show(navigation.getFragmentManager(), "NOTE_CREATE");
                    return true;
                } else if (item.getItemId() == R.id.action_clear) {
                    data.clearNoteData();
                    //Обновляет данные списка.
                    data.init(noteSource -> adapter.notifyDataSetChanged());
                    return true;
                }
                return true;
            });
        });
    }


    public interface Controller {
        void openNoteScreen(Note note, int position);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (!(context instanceof Controller)) {
            throw new RuntimeException("Activity must implement NoteScreen");
        }
    }

    private void initRecyclerView() {

        recyclerView.setHasFixedSize(true);

        // Будем работать со встроенным менеджером
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new NoteAdapter(getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE);
        // Установим адаптер
        recyclerView.setAdapter(adapter);
        // Добавим разделитель карточек
        DividerItemDecoration itemDecoration = new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL);
        itemDecoration.setDrawable(Objects.requireNonNull(ResourcesCompat.getDrawable(getResources(), com.android.notes.R.drawable.seporator, null)));
        recyclerView.addItemDecoration(itemDecoration);
        // Установим анимацию. А чтобы было хорошо заметно, сделаем анимацию долгой
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(MY_DEFAULT_DURATION);
        animator.setRemoveDuration(MY_DEFAULT_DURATION);
        recyclerView.setItemAnimator(animator);
        // Установим слушателя
        adapter.setOnItemClickListener((view, position, itemId) -> {
            view.setBackgroundResource(R.color.teal_700);
            if (itemId == adapter.CMD_UPDATE) {
                ((Controller) requireActivity())
                        .openNoteScreen(adapter.getDataSource().getNoteData(position), position);
            } else if (itemId == adapter.CMD_DELETE) {
                // 1. Создайте диалоговое окно с предупреждением перед удалением данных.

                // Создаём билдер и передаём контекст приложения
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                // В билдере указываем заголовок окна. Можно указывать как ресурс, так
                // и строку
                builder.setTitle(R.string.alert_dialog_delete)
                        // Указываем сообщение в окне. Также есть вариант со строковым
                        // параметром
                        .setMessage(R.string.exclamation)
                        // Из этого окна нельзя выйти кнопкой Back
                        .setCancelable(false)
                        // Устанавливаем отрицательную кнопку
                        .setNegativeButton(R.string.no,
                                // Ставим слушатель, будем обрабатывать нажатие
                                (dialog, id) -> Toast.makeText(context, "Удаление отменено", Toast.LENGTH_SHORT).show())
                        // Устанавливаем кнопку. Название кнопки также можно задавать
                        // строкой
                        .setPositiveButton(R.string.yes,
                                // Ставим слушатель, будем обрабатывать нажатие
                                (dialog, id) -> {
                                    data.deleteNoteData(position);
                                    adapter.notifyDataSetChanged();
                                });
                AlertDialog alert = builder.create();
                alert.show();
            }

        });
    }

    public void addUpdateNote(Note newNote, int position) {

        if (data.size() != position) {
            data.updateNoteData(position, newNote);
        } else data.addNoteData(newNote);
        //метод init ооповещает обозревателей
        data.init(noteSource -> adapter.notifyDataSetChanged());
        //позицианируется на новой позиции
        recyclerView.scrollToPosition(position);
    }

}