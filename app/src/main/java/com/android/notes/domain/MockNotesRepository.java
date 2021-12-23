package com.android.notes.domain;

import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MockNotesRepository implements INotesRepository {
    public static final MockNotesRepository INSTANCE = new MockNotesRepository();

    private final Executor executor = Executors.newCachedThreadPool();
    private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    private List<Note> notes;

    public MockNotesRepository() {
        if (notes == null) {
            getNotesExample(25, new Callback<List<Note>>() {
                @Override
                public void onResult(List<Note> value) {
                    notes = value;
                }
            });
        }
    }

    @Override
    public void getNotes(Callback<List<Note>> callback) {
        //TODO реализовать загрузку заметок откуда-либо
        executor.execute(new Runnable() {
            @Override
            public void run() {
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Collections.sort(notes, new Comparator<Note>() {
                            @Override
                            public int compare(Note o1, Note o2) {
                                Long d1 = o1.getDateCreated();
                                Long d2 = o2.getDateCreated();
                                return d2.compareTo(d1);
                            }
                        });
                        callback.onResult(notes);
                    }
                });
            }
        });
    }

    @Override
    public void getNote(int index, Callback<Note> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Note note;
                if (notes != null && notes.size() > index) {
                    note = notes.get(index);
                } else {
                    note = null;
                }
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(note);
                    }
                });
            }
        });
    }

    @Override
    public void getNotesExample(int count, Callback<List<Note>> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<Note> result = new ArrayList<>();
                Random rnd = new Random();
                Calendar cal;
                List<Integer> colors = Arrays.asList(Color.RED, Color.GREEN, Color.BLACK, Color.BLUE, Color.GRAY, Color.YELLOW);
                List<String> noteExamples = Arrays.asList(
                        "Предварительные выводы неутешительны: современная методология разработки позволяет оценить значение поставленных обществом задач. Прежде всего, современная методология разработки представляет собой интересный эксперимент проверки форм воздействия. Лишь стремящиеся вытеснить традиционное производство, нанотехнологии неоднозначны и будут разоблачены.Предварительные выводы неутешительны: современная методология разработки позволяет оценить значение поставленных обществом задач. Прежде всего, современная методология разработки представляет собой интересный эксперимент проверки форм воздействия. Лишь стремящиеся вытеснить традиционное производство, нанотехнологии неоднозначны и будут разоблачены.",
                        "Имеется спорная точка зрения, гласящая примерно следующее: активно развивающиеся страны третьего мира являются только методом политического участия и описаны максимально подробно. Предварительные выводы неутешительны: глубокий уровень погружения обеспечивает актуальность экспериментов, поражающих по своей масштабности и грандиозности. Являясь всего лишь частью общей картины, явные признаки победы институционализации лишь добавляют фракционных разногласий и объективно рассмотрены соответствующими инстанциями.",
                        "Имеется спорная точка зрения, гласящая примерно следующее: предприниматели в сети интернет лишь добавляют фракционных разногласий и подвергнуты целой серии независимых исследований. С учётом сложившейся международной обстановки, консультация с широким активом требует определения и уточнения форм воздействия. Лишь предприниматели в сети интернет представляют собой не что иное, как квинтэссенцию победы маркетинга над разумом и должны быть заблокированы в рамках своих собственных рациональных ограничений!",
                        "Учитывая ключевые сценарии поведения, базовый вектор развития является качественно новой ступенью кластеризации усилий. Мы вынуждены отталкиваться от того, что постоянное информационно-пропагандистское обеспечение нашей деятельности не оставляет шанса для экономической целесообразности принимаемых решений. В рамках спецификации современных стандартов, диаграммы связей, инициированные исключительно синтетически, преданы социально-демократической анафеме.",
                        "Противоположная точка зрения подразумевает, что ключевые особенности структуры проекта являются только методом политического участия и разоблачены. Учитывая ключевые сценарии поведения, современная методология разработки прекрасно подходит для реализации вывода текущих активов. Семантический разбор внешних противодействий представляет собой интересный эксперимент проверки благоприятных перспектив.",
                        "Безусловно, внедрение современных методик позволяет выполнить важные задания по разработке укрепления моральных ценностей. Значимость этих проблем настолько очевидна, что реализация намеченных плановых заданий однозначно фиксирует необходимость новых предложений. А также действия представителей оппозиции могут быть своевременно верифицированы.",
                        "Значимость этих проблем настолько очевидна, что постоянный количественный рост и сфера нашей активности влечет за собой процесс внедрения и модернизации всесторонне сбалансированных нововведений. Соображения высшего порядка, а также сложившаяся структура организации влечет за собой процесс внедрения и модернизации направлений прогрессивного развития. С другой стороны дальнейшее развитие различных форм деятельности представляет...",
                        "С другой стороны начало повседневной работы по формированию позиции напрямую зависит от всесторонне сбалансированных нововведений. Разнообразный и богатый опыт курс на социально-ориентированный национальный проект обеспечивает актуальность дальнейших направлений развитая системы массового участия. Таким образом, выбранный нами инновационный путь играет важную роль в формировании новых предложений. С другой стороны курс на...",
                        "Соображения высшего порядка, а также начало повседневной работы по формированию позиции требует определения и уточнения форм воздействия. Практический опыт показывает, что дальнейшее развитие различных форм деятельности позволяет оценить значение системы масштабного изменения ряда параметров. С другой стороны новая модель организационной деятельности играет важную роль в формировании существующих финансовых и административных...",
                        "Равным образом реализация намеченного плана развития играет важную роль в формировании форм воздействия. Таким образом, повышение уровня гражданского сознания позволяет выполнить важнейшие задания по разработке системы обучения кадров, соответствующей насущным потребностям. Не следует, однако, забывать о том, что начало повседневной работы по формированию позиции требует определения и уточнения системы обучения...");

                for (int i = 0; i < count; i++) {
                    cal = Calendar.getInstance();
                    cal.add(Calendar.DATE, (-(rnd.nextInt(150))));
                    result.add(new Note(
                            "Заметка №" + i,
                            noteExamples.get(rnd.nextInt(10)),
                            cal.getTimeInMillis(),
                            colors.get(rnd.nextInt(5))
                    ));
                }

                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(result);
                    }
                });
            }
        });
    }

    @Override
    public void updateNote(Note oldNote, Note note, Callback<Boolean> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                notes.set(notes.indexOf(oldNote), note);

                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(true);
                    }
                });
            }
        });
    }

    @Override
    public void deleteNote(Note note, Callback<Boolean> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                notes.remove(note);

                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(true);
                    }
                });
            }
        });

    }

    @Override
    public void addNote(Note note, Callback<Boolean> callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                notes.add(note);
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(true);
                    }
                });
            }
        });
    }
}
