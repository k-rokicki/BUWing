<?php
    $name = $_GET['name'];
    $surname = $_GET['surname'];
    $login = $_GET['login'];
    $password = $_GET['password'];

    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $result = false;

    if (isset($name) && isset($surname) && isset($login) && isset($password) &&
        trim($name) != "" && trim($surname) != "" && trim($login) != "" && trim($password) != "" &&
        mb_strlen($name, 'utf-8') == strlen(preg_replace("/[^\p{L} ]/", '', $name)) &&
        mb_strlen($surname, 'utf-8') == strlen(preg_replace("/[^\p{L}- ]/", '', $surname)) &&
        mb_strlen($login, 'utf-8') == strlen(preg_replace("/[^^\p{L}0-9 ]/", '', $login)) &&
        mb_strlen($password, 'utf-8') == strlen(preg_replace("/[^^\p{L}0-9 ]/", '', $password))) {
        $result = pg_query($link,
                            "INSERT INTO users VALUES (
                            default, '"
                            . pg_escape_string($login) . "', '"
                            . password_hash(pg_escape_string($password), PASSWORD_DEFAULT) . "', '"
                            . pg_escape_string($name) . "', '"
                            . pg_escape_string($surname) .
                            "')");
    }

    $JSONobj->registered = 0;
    if ($result) {
        $JSONobj->registered = 1;
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>