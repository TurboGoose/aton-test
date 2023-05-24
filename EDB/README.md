# Simple in-memory database

## Формулировка

В процессе реализации сервиса проверки данных возникла необходимость в организации
in memory кэша с возможностью быстрого поиска по разным полям.
Структура данных представлена следующим набором полей: 
    
    {
        "account": "234678", //long
        "name": "Иванов Иван Иванович", //string
        "value": "2035.34" //double
    }

Количество записей заранее не определено и может меняться динамически.

Необходимо организовать хранение этих записей в памяти с соблюдением требований:
1. Предоставить возможность добавлять новые записи;
2. Предоставить возможность удалять более не нужные записи;
3. Предоставить возможность изменять запись;
4. Получать полный набор записи по любому из полей с одинаковой алгоритмической сложностью (не медленнее log(n));
5. Выбрать наиболее экономный способ хранения данных в памяти.

## Описание решения
### Объекты записей
Поскольку в формулировке не было объяснено четкой семантики хранимых объектов, то за основу было взято предположение,
что объекты хранят информацию об аккаунтах, а поле `account` представляет собой уникальный идентификатор
записи. В соответствии с этим предположением класс записи в программе носит название `Account`,
а уникальный идентификатор - `id`. Названия полей `name` и `value`, а также их типы были сохранены в неизменном виде.

### Структуры данных хранения записей
В ходе проектирования и разработки было принято решение хранить объекты `Account` в массиве, упорядоченном по полю `id`,
поскольку оно является первичным ключом целочисленного типа, что успешно позволяет выполнять бинарный поиск
за логарифмическое время. Из минусов подобного подхода можно отметить вставку в середину массива и удаление из него
за линейное время, однако это отчасти нивелируется предположением о том, что идентификаторы новых вставляемых записей
будут генерироваться последовательно в возрастающем порядке, а значит вставка будет производиться в конец массива,
что улучшит асимптотику вставки до *O(1)* в среднем случае.

Для поиска по полям `name` и `value` использовались индексы, описание которых можно найти ниже.  
Поскольку для каждого из этих полей справедливо отношение один-ко-многим по отношению к записям, 
то в индексах хранятся коллекции ссылок на записи.

Для строкового поля `name` в качестве структуры данных индекса было выбрано префиксное дерево `Trie`,
которое обеспечивает эффективное хранение строк за счет исключения повторов символов. Временная сложность операций
поиска, вставки и удаления составляет *O(K)*, где *K* – длина передаваемой строки. Другими словами, сложность операций
не зависит от количества данных, хранящихся в базе, что позволяет выполнять эффективный поиск даже при большом их
количестве.

Для поля `value`, представленного типом с плавающей точкой, в качестве индекса было решено использовать красно-черное
дерево, а конкретно его реализацию в коллекции `TreeMap`. Данная структура по сравнению с ее предшественником и
главным конкурентом – AVL деревом – обеспечивает наименее избыточное хранение данных, а также более оптимизированные
операции вставки и удаления за счет отсутствия необходимости в избыточных поворотах дерева. Операции
поиска, вставки и удаления в этой структуре выполняются за время *O(log(N))*.


