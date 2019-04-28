<?php
    $ini = parse_ini_file("database_credentials.ini");

    $login = $_POST["login"];
    $password = $_POST["password"];

    $JSONobj->taken = false;
    $JSONobj->seatId = -1;
    $JSONobj->seatFloor = -1;

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
                        "SELECT id, floor
                        FROM tables
                        WHERE taken = TRUE
                        AND userid = " . $row["id"]);

            if (pg_num_rows($result) == 1) {
                $row = pg_fetch_array($result, 0);
                $JSONobj->taken = true;
                $JSONobj->seatId = $row["id"];
                $JSONobj->seatFloor = $row["floor"];
            }
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>