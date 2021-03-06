<?php
    $ini = parse_ini_file("database_credentials.ini");

    $login = $_POST["login"];
    $password = $_POST["password"];

    $JSONobj->invitationsCount = 0;

    $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);
    $result = pg_query($link,
                        "SELECT id, password
                        FROM users
                        WHERE login = '" . pg_escape_string($login) . "'");
    $count = pg_num_rows($result);

    if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $userId = $row["id"];
        $hashedPassword = $row["password"];
        if (password_verify($password, $hashedPassword)) {
            $result = pg_query($link,
                        "SELECT COUNT(*) as count
                        FROM friends
                        WHERE status = FALSE
                        AND inviteeid = " . $userId);

            if (pg_num_rows($result) == 1) {
                $row = pg_fetch_array($result, 0);
                $JSONobj->invitationsCount = $row["count"];
            }
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>