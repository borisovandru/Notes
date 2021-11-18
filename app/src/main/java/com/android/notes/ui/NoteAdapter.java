package com.android.notes.ui;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.BaseViewHolder> {
    private com.android.notes.domain.NoteSource dataSource;
    private OnItemClickListener itemClickListener;  // Слушатель будет устанавливаться извне
    public final int CMD_UPDATE = 0;
    public final int CMD_DELETE = 1;
    private static final int NOTE_VIEW_TYPE = 1;
    private static final int GROUP_VIEW_TYPE = 0;
    private final boolean isLandscape;

    public NoteAdapter(boolean isLandscape) {
        this.isLandscape = isLandscape;
    }

    // Передаём в конструктор источник данных
    // Внесла коррективы в этот класс.
    // Изменили конструктор, добавили обозревателя.
    @android.annotation.SuppressLint("NotifyDataSetChanged")
    public void setDataSource(com.android.notes.domain.NoteSource dataSource) {
        this.dataSource = dataSource;
        notifyDataSetChanged();
    }

    // Создать новый элемент пользовательского интерфейса
    // Запускается менеджером
    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public NoteAdapter.BaseViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        // Создаём новый элемент пользовательского интерфейса
        // Через Inflater

        switch (viewType) {
            case NOTE_VIEW_TYPE:
                View vNote = LayoutInflater.from(viewGroup.getContext())
                        .inflate(com.android.notes.R.layout.item, viewGroup, false);
                // Здесь можно установить всякие параметры
                return new NoteViewHolder(vNote);
            case GROUP_VIEW_TYPE:
                View vGroup = LayoutInflater.from(viewGroup.getContext())
                        .inflate(com.android.notes.R.layout.group_title, viewGroup, false);
                // Здесь можно установить всякие параметры
                return new GroupViewHolder(vGroup);
            default:
                throw new RuntimeException("Не верно указан тип View holder");
        }
    }

    // Заменить данные в пользовательском интерфейсе
    // Вызывается менеджером
    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.BaseViewHolder viewHolder, int i) {
        // Получить элемент из источника данных (БД, интернет...)
        // Вынести на экран, используя ViewHolder
        viewHolder.setData(dataSource, i);
    }

    @Override
    public int getItemViewType(int position) {
        return NOTE_VIEW_TYPE;
        //return (position==0) ? GROUP_VIEW_TYPE : NOTE_VIEW_TYPE;
    }

    // Вернуть размер данных, вызывается менеджером
    @Override
    public int getItemCount() {
        return dataSource.size();
    }

    // Интерфейс для обработки нажатий, как в ListView
    public interface OnItemClickListener {
        void onItemClick(View view, int position, int itemId);
    }

    public com.android.notes.domain.NoteSource getDataSource() {
        return dataSource;
    }

    // Сеттер слушателя нажатий
    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    // Этот класс хранит связь между данными и элементами View
    // Сложные данные могут потребовать несколько View на один пункт списка
    public class NoteViewHolder extends BaseViewHolder {

        private final TextView titleTV;
        private final TextView descriptionTV;

        @RequiresApi(api = Build.VERSION_CODES.N)
        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTV = itemView.findViewById(com.android.notes.R.id.title);
            descriptionTV = itemView.findViewById(com.android.notes.R.id.description);
            AppCompatImageView image = itemView.findViewById(com.android.notes.R.id.imageView);

            // Обработчик нажатий на этом ViewHolder
            image.setOnClickListener(v -> {
                if (isLandscape && itemClickListener != null) {
                    itemClickListener.onItemClick(v, getAdapterPosition(), CMD_UPDATE);
                } else {
                    image.showContextMenu(getAdapterPosition(), getAdapterPosition());
                }
            });

            image.setOnCreateContextMenuListener((menu, v, menuInfo) -> {

                menu.setHeaderTitle(com.android.notes.R.string.item_title);
                menu.add(0, CMD_UPDATE, 0, com.android.notes.R.string.item_update).
                        setOnMenuItemClickListener(item -> {
                            if (itemClickListener != null) {
                                itemClickListener.onItemClick(v, getAdapterPosition(), CMD_UPDATE);
                            }
                            return true;
                        });

                menu.add(0, CMD_DELETE, 0, com.android.notes.R.string.item_delete).
                        setOnMenuItemClickListener(item -> {
                            if (itemClickListener != null) {
                                itemClickListener.onItemClick(v, getAdapterPosition(), CMD_DELETE);
                            }
                            return true;
                        });

            });
        }

        public void setData(com.android.notes.domain.NoteSource noteSourceImp, int position) {
            com.android.notes.domain.Note note = noteSourceImp.getNoteData(position);
            titleTV.setText(note.getName());
            descriptionTV.setText(note.getDescription());
        }
    }

    public static class GroupViewHolder extends BaseViewHolder {

        private final TextView title;

        public GroupViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(com.android.notes.R.id.group_title);
        }

        public void setData(com.android.notes.domain.NoteSource noteSourceImp, int position) {
            title.setText(com.android.notes.domain.User.nameUser);
        }
    }

    public abstract static class BaseViewHolder extends RecyclerView.ViewHolder {
        public BaseViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(com.android.notes.domain.NoteSource noteSourceImp, int position) {
        }
    }


}