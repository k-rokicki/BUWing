<?php
    $nrindeksu = $_GET['nrindeksu'];
    $haslo = $_GET['haslo'];

    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $wynik = pg_query($link,
                        "SELECT imie, nazwisko
                        FROM uzytkownicy
                        WHERE nrindeksu = '" . pg_escape_string($nrindeksu) .
                        "' AND haslo = '" . pg_escape_string($haslo) . "'");
    $ile = pg_numrows($wynik);

    $myObj->zalogowano = 0;
    $myObj->imie = "";
    $myObj->nazwisko = "";

    if ($ile == 1) {
        $wiersz = pg_fetch_array($wynik, 0);
        $myObj->zalogowano = 1;
        $myObj->imie = $wiersz["imie"];
        $myObj->nazwisko = $wiersz["nazwisko"];
    }

    pg_close($link);

    $myJSON = json_encode($myObj);
    echo $myJSON;
?>