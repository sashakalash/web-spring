# Задача
Необходимо отрефакторить код, рассмотренный на лекции, и применить все те знания, которые у вас есть:

* Выделить класс Server с методами для запуска обработки конкретного подключения
* Реализовать обработку подключений с помощью ThreadPool'а (выделите фиксированный на 64 потока и каждое подключение обрабатывайте в потоке из пула)

Поскольку вы - главный архитектор и проектировщик данного небольшого класса, то все архитектурные решения принимать вам, но будьте готовы к критике со стороны проверяющих.


### Легенда
Сервер, который вы написали в предыдущей задаче, - это, конечно, здорово, но пока он не расширяем и его нельзя переиспользовать, т.к. код обработки зашит прямо внутрь сервера.

Давайте попробуем его сделать немного полезнее.

Что хотим сделать? Мы хотим сделать так, чтобы в сервер можно было добавлять обработчики на определённые шаблоны путей.
