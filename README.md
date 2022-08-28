# Excel-porownywarka
Program służy do porównywania plików Excel - wyznaczania modyfikacji, usunięć bądź dodań. Zmiany są nakreślane kolorem czerwonym w zmodyfikowanym pliku.

## Uwaga 
Przed rozpoczęciem porównywania należy zrobić kopię zapasową porównywanych plików.

## Uruchamianie
Od wersji 1.2 program posiada 2 tryby pracy: konsolowy (CLI) oraz okienkowy (GUI).
Aby go uruchomić w trybie okienkowym wystaczy standardowo nacisnąć dwa razy na ikonę. Alternatywnie można użyć komendy z konsoli bez dodania dodatkowych argumentów:

```bash
java -jar Excel-porownywarka-1.2.jar
```

Aby uruchomić program w trybie konsoli (CLI) należy dodać `--cli` do komendy startowej:
```bash
java -jar Excel-porownywarka-1.2.jar --cli
```

Dla wersji starszych niż 1.2 zalecane jest utworzenie skryptu uruchomieniowego (np. start.bat dla Windowsa):
```bash
@ECHO OFF
java -jar Excel-porownywarka-1.1.jar
PAUSE
```


Jeżeli masz problem z uruchomieniem programu w konsoli lub przy pomocy np. IDE, należy dodać flagę `--cli` oraz `--ignore-console` do komendy startowej
```bash
java -jar Excel-porownywarka-1.2.jar --cli --ignore-console
```
## Wymagania
- Java 8 lub nowsza
- W przypadku dużych plików program może pochłonąć duże ilości pamięci RAM (1+ GB)
- Szybkość porównywania plików zależy w głównej mierze od szybkości pojedynczego wątku procesora
