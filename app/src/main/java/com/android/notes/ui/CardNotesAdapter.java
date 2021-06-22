package com.android.notes.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.notes.R;
import com.android.notes.domain.CardData;
import com.android.notes.domain.CardSource;

public class CardNotesAdapter extends RecyclerView.Adapter<CardNotesAdapter.ViewHolder> {
    private final static String TAG = "SocialNetworkAdapter";
    private final CardSource cardSource;

    public CardNotesAdapter(CardSource cardSource) {
        this.cardSource = cardSource;
    }

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Создаём новый элемент пользовательского интерфейса
        // Через Inflater
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        // Здесь можно установить всякие параметры
        Log.d(TAG, "onCreateViewHolder");
        return new ViewHolder(v);
    }

    // Заменить данные в пользовательском интерфейсе
    // Вызывается менеджером
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Получить элемент из источника данных (БД, интернет...)
        // Вынести на экран, используя ViewHolder
        holder.setData(cardSource.getCardData(position));
    }

    // Вернуть размер данных, вызывается менеджером
    @Override
    public int getItemCount() {
        return cardSource.size();

    }

    // Этот класс хранит связь между данными и элементами View
    // Сложные данные могут потребовать несколько View на один пункт списка
    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView title;
        private final TextView description;
        private final AppCompatImageView image;
        private CheckBox like;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.itemTextViewName);
            description = itemView.findViewById(R.id.itemTextViewDescription);
            image = itemView.findViewById(R.id.itemImageView);


            image.setOnClickListener(view -> {
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(view, getAdapterPosition());
            });
        }

        public void setData(CardData cardData) {
            title.setText(cardData.getName());
            description.setText(cardData.getDescription());
            image.setImageResource(cardData.getPicture());
        }


    }
}
