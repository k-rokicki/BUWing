<?php
    $name = $_GET['name'];
    $surname = $_GET['surname'];
    $login = $_GET['login'];
    $password = $_GET['password'];

    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $result = false;
    $JSONobj->registered = 0;

    if (isset($name) && isset($surname) && isset($login) && isset($password) &&
        trim($name) != "" && trim($surname) != "" && trim($login) != "" && trim($password) != "") {
        
        $result = pg_query($link,
                                "SELECT COUNT(*)
                                FROM users
                                WHERE login = '" . pg_escape_string($login) . "'");

        $row = pg_fetch_array($result, 0);
        $loginAlreadyUsed = $row[0];
        
        if ($loginAlreadyUsed) {
            $JSONobj->registered = -1;
        } else {
            $result = pg_query($link,
                                    "INSERT INTO users VALUES (
                                    default, '"
                                    . pg_escape_string($login) . "', '"
                                    . password_hash($password, PASSWORD_DEFAULT) . "', '"
                                    . pg_escape_string($name) . "', '"
                                    . pg_escape_string($surname) .
                                    "')");

            if ($result) {
                $JSONobj->registered = 1;
            }
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>