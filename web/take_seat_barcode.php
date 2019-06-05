<?php

    header('Access-Control-Allow-Origin: *');
    header('Access-Control-Allow-Methods: GET, POST');
    header("Access-Control-Allow-Headers: X-Requested-With");
    
    $ini = parse_ini_file("database_credentials.ini");

    $login = $_POST["login"];
    $password = $_POST["password"];
    $barcode = $_POST["barcode"];

    $JSONobj->took = 0;
    $JSONobj->table = -1;
    $JSONobj->floor = -1;

    $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);
    $result = pg_query($link,
                        "SELECT id, password
                        FROM users
                        WHERE login = '" . pg_escape_string($login) . "'");
    $count = pg_num_rows($result);

    if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $hashedPassword = $row["password"];
        if (password_verify($password, $hashedPassword)) {
            $result = pg_query($link,
                        "UPDATE tables
                         SET taken = TRUE,
                         userid = " . $row["id"] . "
                         WHERE barcode = '" . $barcode . "'
                         AND taken = FALSE
                         RETURNING id, floor");

            if (pg_num_rows($result) != 1) {
                $JSONobj->took = -1;
            } else {
                $JSONobj->took = 1;
                $row = pg_fetch_array($result, 0);
                $JSONobj->table = $row["id"];
                $JSONobj->floor = $row["floor"];
            }
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>