# Excel-porownywarka
Program służy do porównywania plików Excel - wyznaczania modyfikacji, usunięć bądź dodań. Zmiany są nakreślane kolorem czerwonym w zmodyfikowanym pliku.

## Uwaga 
Przed rozpoczęciem porównywania należy zrobić kopię zapasową porównywanych plików.

## Uruchamianie
Program jest przeznaczony do używania poprzez konsolę. Aby go uruchomić musimy użyć polecenia:

```bash
java -jar Excel-porownywarka-1.1.jar
```

Zalecane jest utworzenie skryptu uruchomieniowego (np. start.bat):
```bash
@ECHO OFF
java -jar Excel-porownywarka-1.1.jar
PAUSE
```


Bezpośrednie uruchomienie (kliknięciem na ikonę) skutkuje błędem o braku konsoli. Jeżeli błąd występuje przy uruchamianiu komendą lub przy pomocy np. IDE, należy dodać flagę `--ignore-console` do komendy startowej
```bash
java -jar Excel-porownywarka-1.1.jar --ignore-console
```
