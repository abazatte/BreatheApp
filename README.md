# Breathe App

Das Projekt wurde mit **Android Studio** mit der Programmiersprache **Java** programmiert.

![grafik (6).png](Projekt%20Re%20f45a1/grafik_(6).png)

## Allgemeines

Es wird das Handy Pixel 3 oder Pixel 5 empfohlen.

## Beim Start

Wenn man die App neu buildet, dann wird die Datenbank mit **Dummy-Daten** gefüllt, diese sind absichtlich da und soll das Testen der App erleichtern. Sie können die Daten updaten oder sogar direkt eine Übung ausführen. 
Der **Control Pause Tracker** beinhaltet das Luftanhalten und es wird lediglich der Rekord im Monat angezeigt. Die Grafik, die dort zu sehen ist, wird wie bei den Atemübungen beim ersten builden festgelegt, sie dienen hier wieder als Dummy-Werte.

![Untitled](Projekt%20Re%20f45a1/Untitled.png)

Beim Starten erhält man diese View, weil wir uns darüber Gedanken gemacht haben, wie wir dem Nutzer deutlich machen können, dass es noch eine weitere Funktion in der Anwendung existieren.

## Ausführung

### Übung- und Fragmenteditor

Um Atemübungen zu erstellen, muss man auf den Plus-Button drücken.

![Untitled](Projekt%20Re%20f45a1/Untitled%201.png)

Durch betätigen des Buttons wird eine View geöffnet, wo man den Titel, die Beschreibung, die Priorität, Anzahl der Wiederholungen oder die Zeit im genauen angeben kann.

![Untitled](Projekt%20Re%20f45a1/Untitled%202.png)

Diese View entnimmt die eingegebenen Daten der Nutzer und speichert diese in die Datenbank ab. 

Die Priorität der Übung dient zum sortieren der Atemübungen. Je niedriger diese ist, desto höher wird sie im RecyclerView sein.

Anzahl der Wiederholungen ist lediglich, wie oft die Liste mit den Fragmenten durchgangen werden soll.

Jedoch kann man wählen, ob eine festgelegte Zeit benutzt werden soll, oder ob die Liste durchlaufen werden soll.

Zudem kann man bei jeder Übung die Animation wählen.

![Untitled](Projekt%20Re%20f45a1/Untitled%203.png)

Die einzelnen Fragmente sind Items, die man anklicken kann, so wird das Editfenster geöffnet und dort kann man diese dann auch anpassen.

![Untitled](Projekt%20Re%20f45a1/Untitled%204.png)

Ein Fragment besteht aus dem Titel, der Priotität, den Zeiten für Einatmen, Anhalten, Ausatmen, Aushalten und der Anzahl der Wiederholungen. Die Wiederholung bezieht sich für die Zeiten.

### Starten der Übung

Wir haben uns für zwei Animationen entschieden, die Progress Bar und das Polygon. 

![Untitled](Projekt%20Re%20f45a1/Untitled%205.png)

Dort sieht man die Progress Bar, die Übung wird durch das klicken auf “Start” gestartet, der Timer oben wird runtergespielt und die Progress Bar entnimmt die Zeiten aus den Fragmenten und führt so visuell das aus, was gerade Sache ist. Das füllen soll dabei das Einatmen darstellen.

![Untitled](Projekt%20Re%20f45a1/Untitled%206.png)

Hier ist die Animation mit Polygon, diese soll ebenso visuell darstellen, was gerade gemacht werden soll, das Größerwerden  bzw. das Kleinerwerden des Polygons soll dabei das Einatmen/Ausatmen darstellen, das Ändern der Farben soll das Anhalten bzw Aushalten symbolisieren. 

![Untitled](Projekt%20Re%20f45a1/Untitled%207.png)

Beginn Einatmen

![Untitled](Projekt%20Re%20f45a1/Untitled%208.png)

Beginn Anhalten

![Untitled](Projekt%20Re%20f45a1/Untitled%209.png)

Beginn Ausatmen

![Untitled](Projekt%20Re%20f45a1/Untitled%2010.png)

Beginn Aushalten

Hier sieht man die 4 Phasen der Atemübung.

Das Löschen einer Übung wird mit dem swipen realisiert, da man auch ausversehen eine Übung wegswipen könnte, wird diese mit einem Alert abgefragt.

![Untitled](Projekt%20Re%20f45a1/Untitled%2011.png)

Man kann ebenfalls alle Übungen auf einmal Löschen, diese wird  oben rechts gehalten und führt ebenso zu einem Alert Dialog.

### Control Pause

Die View, welche geöffnet wird, wenn man auf Control Pause Tracker geht, zeigt ein Diagramm, welche den aktuellen Monat ganz rechts hält und die Monatsbestzeit anzeigt. 

![Untitled](Projekt%20Re%20f45a1/Untitled%2012.png)

(Dummy-Werte, die wurden Randomized, werden beim starten auf einem anderen Emulator sehr wahrscheinlich andere Werte innehaben.) Zu sehen sind die Diagramme und ein Button, der Button führt zur Ausführungsview.

![Untitled](Projekt%20Re%20f45a1/Untitled%2013.png)

Hier kann man den Timer starten und es wird beim klicken auf reset zurückgesetzt. Zudem steht die Zeit dann in Sekunden bei save und man kann diese dann in die Datenbank abspeichern. Wenn die Zeit mehr ist, wie bei dem vorherigen Rekord, so wird diese dann direkt sichtbar.

Beispielablauf:

![Untitled](Projekt%20Re%20f45a1/Untitled%2014.png)

Beginn

![Untitled](Projekt%20Re%20f45a1/Untitled%2015.png)

Man hat 15 Sekunden die Luft angehalten

![Untitled](Projekt%20Re%20f45a1/Untitled%2016.png)

Man jat reset gedrückt und man erhält die Moglichkeit zu speichern
