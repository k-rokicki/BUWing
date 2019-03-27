<?php
    $ini = parse_ini_file("database_credentials.ini");

    $login = $_POST["login"];
    $password = $_POST["password"];

    $JSONobj->loggedin = 0;
    $JSONobj->name = "";
    $JSONobj->surname = "";

    $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);
    $result = pg_query($link,
                        "SELECT password, activated
                        FROM users
                        WHERE login = '" . pg_escape_string($login) . "'");
    $count = pg_num_rows($result);

    if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $hashedPassword = $row["password"];
        $activated = $row["activated"];
        if (password_verify($password, $hashedPassword) && $activated) {
            $result = pg_query($link,
                        "SELECT name, surname, email
                        FROM users
                        WHERE login = '" . pg_escape_string($login) . "'");
            $row = pg_fetch_array($result, 0);
            $JSONobj->loggedin = 1;
            $JSONobj->name = $row["name"];
            $JSONobj->surname = $row["surname"];
            $JSONobj->email = $row["email"];
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>