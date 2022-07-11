# Rbyten_kt

<h3>
  Android-приложение для создания схем рабочих процессов <br> 
  UI создан с использованием Jetpack Compose
</h3>

Приложение позволяет разбить любую сложную задачу или процесс на его составные части и отслеживать их в виде наглядной схемы

## Главный экран

На Главном экране хранятся созданные пользователем контейнеры для схем - так называемые "чертежи".

<p>
<img src="https://github.com/Sanders25/Rbyten_kt/blob/master/demo/Screenshot_2022-06-07-18-41-42-010_com.example.rbyten.jpg" width="30%" height="30%" style="float: left">
<img src="https://github.com/Sanders25/Rbyten_kt/blob/master/demo/main.gif" width="30%" height="30%" style="float: right">
</p>

## Меню создания чертежа

Создать чертёж можно с помощью меню, открывающегося при нажатии на центральную кнопку.

<img src="https://github.com/Sanders25/Rbyten_kt/blob/master/demo/Screenshot_2022-06-07-18-56-36-171_com.example.rbyten.jpg" width="30%" height="30%">

## Окно редактора и задача

При нажатии на любой чертёж открывается окно редактора. Окно редактора свободно перемещается стандартными жестами. 
Также поддерживается изменение масштаба.

В окне редактора с помощью аналогичной кнопки можно создавать специальные карточки - "задачи".
Каждая задача является контейнером для виджетов.

Виджет можно добавить с помощью меню задачи. На данный момент это либо текстовое поле, либо список. Виджеты можно добавлять и удалять в любом порядке. 

После каждого изменения происходит автосохранение чертежа.
<p>
<img src="https://github.com/Sanders25/Rbyten_kt/blob/master/demo/Screenshot_2022-06-07-20-05-22-070_com.example.rbyten.jpg" width="30%" height="30%" style="float: left">
<img src="https://github.com/Sanders25/Rbyten_kt/blob/master/demo/editor.gif" width="30%" height="30%" style="float: right">
</p>

## Пример схемы

<p>
<img src="https://github.com/Sanders25/Rbyten_kt/blob/master/demo/Screenshot_2022-06-07-18-42-22-783_com.example.rbyten.jpg" width="30%" height="30%" style="float: left">
<img src="https://github.com/Sanders25/Rbyten_kt/blob/master/demo/map.gif" width="30%" height="30%" style="float: right">
</p>
