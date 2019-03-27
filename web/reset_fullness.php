<?php
    $ini = parse_ini_file("database_credentials.ini");

    $login = $_GET['login'];
    $password = $_GET['password'];

    $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);
    $result = pg_query($link,
                        "SELECT password
                        FROM admins
                        WHERE login = '" . pg_escape_string($login) . "'");
    $count = pg_num_rows($result);

    if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $hashedPassword = $row["password"];
        if (password_verify($password, $hashedPassword)) {
            $result = pg_query($link,
                        "UPDATE tables
                        SET taken = FALSE");
        }
    }

    pg_close($link);
?>