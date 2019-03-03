<?php
    $login = $_GET['login'];
    $password = $_GET['password'];

    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $result = pg_query($link,
                        "SELECT name, surname
                        FROM users
                        WHERE login = '" . pg_escape_string($login) .
                        "' AND password = '" . pg_escape_string($password) . "'");
    $count = pg_numrows($result);

    $JSONobj->loggedin = 0;
    $JSONobj->name = "";
    $JSONobj->surname = "";

    if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $JSONobj->loggedin = 1;
        $JSONobj->name = $row["name"];
        $JSONobj->surname = $row["surname"];
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>