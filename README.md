# Инструкция по сборке и запуску проекта
https://libgdx.com/dev/import-and-running/

Так же в папке release уже имеется jar файл, который можно запустить без IDE.
# Управление
Корабль всегда смотрит в направлении курсора.

W/A/D - движение вперёд/влево/вправо

ЛКМ - выстрел (при попадании лазером по самому себе корабль теряет управление на короткое время)

F1 - пауза, показать управление

F5 - рестарт игры

F6 - добавить астероид

F7 - вкл/выкл отображение отладочной информации

F11 - вкл/выкл полный экран

# Дополнительная информация
Весь код в папке [/core/src/com/codeandweb/physicseditor/](https://github.com/TEXH4Pb/java-developer-test-frabynin/tree/main/core/src/com/codeandweb/physicseditor) не мой. Импортировал для использования редактора коллижн моделей объектов.
Подробнее тут: https://www.codeandweb.com/physicseditor.
# Известные проблемы и возможные пути их решения
### При изменении размеров окна во время игры объекты остаются на прежних местах.
Для устранения надо было масштабировать координаты объектов в соответствии с новым разрешением окна, но т.к. ошибка не критичная, решил не тратить на это время.

### Переход на объектов на противоположную сторону экрана происходит резко, зачастую объекту достаточно выйти за пределы неполностью.
При проверке местоположения объекта, нужно к границам мира добавлять/отнимать размеры проверяемого тела, но так и не нашёл, как получить из body его габариты.

### Размер экрана влияет на размер физического мира, на небольших разрешениях экрана игра сложнее
Единственный выход- задать миру фиксированные размеры и подгонять картинку под размеры экрана, либо масштабируя картинку, либо оставляя поля по бокам. Первый вариант выглядит некрасиво, а второй не соответствует ТЗ. Поскольку задачи сделать киберспортивную дисциплину не было, сделал выбор в пользу красивой картинки, а не баланса. :^)
