<?php
    $login = $_POST["login"];
    $oldPassword = $_POST["oldPassword"];
    $newPassword = $_POST["newPassword"];

    $JSONobj->updated = 0;

    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $result = pg_query($link,
                        "SELECT password
                        FROM users
                        WHERE login = '" . pg_escape_string($login) . "'");
    $count = pg_num_rows($result);

    if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $hashedPassword = $row["password"];
        if (password_verify($oldPassword, $hashedPassword)) {
            $result = pg_query($link,
                        "UPDATE users
                        SET password = '" . password_hash($newPassword, PASSWORD_DEFAULT) .
                        "' WHERE login = '" . pg_escape_string($login) . "'");
            if ($result) {
                $JSONobj->updated = 1;
            }
        } else {
            $JSONobj->updated = -1;
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>