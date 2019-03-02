<?php
    $imie = $_GET['imie'];
    $nazwisko = $_GET['nazwisko'];
    $nrindeksu = $_GET['nrindeksu'];
    $haslo = $_GET['haslo'];

    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $wynik = false;

    if (isset($imie) && isset($nazwisko) && isset($nrindeksu) && isset($haslo) &&
        trim($imie) != "" && trim($nazwisko) != "" && trim($nrindeksu) != "" && trim($haslo) != "" &&
        $nrIndeksu == preg_replace("/[^0-9 ]/", '', $nrIndeksu) &&
        mb_strlen($imie, 'utf-8') == strlen(preg_replace("/[^\p{L} ]/", '', $imie)) &&
        mb_strlen($nazwisko, 'utf-8') == strlen(preg_replace("/[^\p{L}- ]/", '', $nazwisko)) &&
        mb_strlen($haslo, 'utf-8') == strlen(preg_replace("/[^^\p{L}0-9 ]/", '', $haslo))) {
        $wynik = pg_query($link,
                            "INSERT INTO uzytkownicy VALUES (
                            default, '"
                            . pg_escape_string($nrindeksu) . "', '"
                            . pg_escape_string($haslo) . "', '"
                            . pg_escape_string($imie) . "', '"
                            . pg_escape_string($nazwisko) .
                            "')");
    }

    $myObj->zarejestrowano = 0;
    if ($wynik) {
        $myObj->zarejestrowano = 1;
    }

    pg_close($link);

    $myJSON = json_encode($myObj);
    echo $myJSON;
?>