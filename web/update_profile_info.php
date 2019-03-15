<?php
    $login = $_POST["login"];
    $password = $_POST["password"];
    $newName = $_POST["newName"];
    $newSurname = $_POST["newSurname"];
    $newLogin = $_POST["newLogin"];

    $JSONobj->updated = false;
    $JSONobj->name = "";
    $JSONobj->surname = "";
    $JSONobj->login = "";

    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $result = pg_query($link,
                        "SELECT password
                        FROM users
                        WHERE login = '" . pg_escape_string($login) . "'");
    $count = pg_num_rows($result);

    if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $hashedPassword = $row["password"];
        if (password_verify($password, $hashedPassword)) {
            $result = pg_query($link,
                        "UPDATE users
                        SET name = '" . pg_escape_string($newName) .
                        "', surname = '" . pg_escape_string($newSurname) .
                        "', login = '" . pg_escape_string($newLogin) .
                        "' WHERE login = '" . pg_escape_string($login) .
                        "' RETURNING name, surname, login");
            $row = pg_fetch_array($result, 0);
            if ($result) {
                $JSONobj->updated = true;
                $JSONobj->name = $row["name"];
                $JSONobj->surname = $row["surname"];
                $JSONobj->login = $row["login"];
            }
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>