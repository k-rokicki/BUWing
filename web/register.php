<?php

    function randomString($length) {
        $str = "";
        $characters = array_merge(range('A','Z'), range('a','z'), range('0','9'));
        $max = count($characters) - 1;
        for ($i = 0; $i < $length; $i++) {
            $rand = mt_rand(0, $max);
            $str .= $characters[$rand];
        }
        return $str;
    }

    $ini = parse_ini_file("database_credentials.ini");

    $name = $_POST["name"];
    $surname = $_POST["surname"];
    $login = $_POST["login"];
    $email = $_POST["email"];
    $password = $_POST["password"];

    $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);
    $result = false;
    $JSONobj->registered = 0;

    if (isset($name) && isset($surname) && isset($login) && isset($email) && isset($password) &&
        trim($name) != "" && trim($surname) != "" && trim($login) != "" &&
        trim($email) != "" && trim($password) != "") {

        $result = pg_query($link,
                                "SELECT COUNT(*)
                                FROM users
                                WHERE login = '" . pg_escape_string($login) .
                                "' OR email = '" . pg_escape_string($email) . "'");

        $row = pg_fetch_array($result, 0);
        $loginOrPasswordAlreadyUsed = $row[0];

        if ($loginOrPasswordAlreadyUsed) {
            $JSONobj->registered = -1;
        } else {
            $result = pg_query($link,
                                    "INSERT INTO users VALUES (
                                    default, '"
                                    . pg_escape_string($login) . "', '"
                                    . password_hash($password, PASSWORD_DEFAULT) . "', '"
                                    . pg_escape_string($name) . "', '"
                                    . pg_escape_string($surname) . "', '"
                                    . pg_escape_string($email) .
                                    "') RETURNING id");

            if ($result) {
                $row = pg_fetch_array($result, 0);
                $userid = $row[0];
                $token = randomString(50);

                $result = pg_query($link,
                                    "INSERT INTO activationTokens VALUES ("
                                    . $userid . ", '"
                                    . $token .
                                    "')");

                if ($result) {
                    $JSONobj->registered = 1;

                    $confirmationLink = "http://students.mimuw.edu.pl/~kr394714/buwing/activate.php?userid="
                                        . $userid . "&token=" . $token;

                    $subject = "Aktywuj konto BUWing";

                    $headers = "From: noreply@buwing.com\r\n";
                    $headers .= "MIME-Version: 1.0\r\n";
                    $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

                    $message = '<p>' . $name . ' ' . $surname . ', dziękujemy za rejestrację konta w BUWing.</p>
                                <p>Aktywuj konto klikając w ten <a href="' . $confirmationLink . '">link (ważny 30 dni).</a></p>';

                    mail($email, $subject, $message, $headers);
                }
            }
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>
