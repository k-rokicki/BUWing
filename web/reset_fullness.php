<?php
    $login = $_GET['login'];
    $password = $_GET['password'];

    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $result = pg_query($link,
                        "SELECT *
                        FROM admins
                        WHERE login = '" . pg_escape_string($login) .
                        "' AND password = '" . pg_escape_string($password) . "'");
    $count = pg_numrows($result);

    if ($count == 1) {
        $result = pg_query($link,
                        "UPDATE tables
                        SET taken = FALSE");
    }

    pg_close($link);
?>