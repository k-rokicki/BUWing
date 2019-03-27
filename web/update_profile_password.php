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

    $login = $_POST["login"];
    $oldPassword = $_POST["oldPassword"];
    $newPassword = $_POST["newPassword"];

    $JSONobj->updated = 0;

    $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);
    $result = pg_query($link,
                        "SELECT id, name, surname, email, password
                        FROM users
                        WHERE login = '" . pg_escape_string($login) . "'");
    $count = pg_num_rows($result);

    if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $userid = $row["id"];
        $name = $row["name"];
        $surname = $row["surname"];
        $email = $row["email"];
        $hashedPassword = $row["password"];
        
        if (password_verify($oldPassword, $hashedPassword)) {
            $result = pg_query($link,
                        "SELECT COUNT(*)
                        FROM pendingPasswordChanges
                        WHERE userid = " . $userid);
            $row = pg_fetch_array($result, 0);
            $alreadyPending = $row[0];

            $token = randomString(50);
            
            if ($alreadyPending == 0) {
                $result = pg_query($link,
                            "INSERT INTO pendingPasswordChanges VALUES (" .
                            $userid . ", '" .
                            password_hash($newPassword, PASSWORD_DEFAULT) . "', '" .
                            $token . "')");
            } else {
                $result = pg_query($link,
                            "UPDATE pendingPasswordChanges
                            SET password = '" . password_hash($newPassword, PASSWORD_DEFAULT) .
                            "', token = '" . $token .
                            "' WHERE userid = " . $userid);
            }

            if ($result) {
                $JSONobj->updated = 1;

                $confirmationLink = "http://students.mimuw.edu.pl/~kr394714/buwing/confirm_password_change.php?userid="
                                        . $userid . "&token=" . $token;

                $subject = "Próba zmiany hasła w BUWing";

                $headers = "From: noreply@buwing.com\r\n";
                $headers .= "MIME-Version: 1.0\r\n";
                $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

                $message = '<p>' . $name . ' ' . $surname . ', odnotowaliśmy próbę zmiany hasła do konta w BUWing.</p>
                            <p>Aby potwierdzić zmianę kliknij w ten <a href="' . $confirmationLink . '">link</a></p>';

                mail($email, $subject, $message, $headers);
            }
        } else {
            $JSONobj->updated = -1;
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>