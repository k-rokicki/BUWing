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

    $email = $_POST["email"];
    $JSONobj->resetLinkSent = false;
    
    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $result = pg_query($link,
                        "SELECT id, name, surname
                        FROM users
                        WHERE email = '" . pg_escape_string($email) . "'");
    $count = pg_num_rows($result);

    if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $userid = $row["id"];
        $name = $row["name"];
        $surname = $row["surname"];
        $token = randomString(50);

        $result = pg_query($link,
                        "SELECT COUNT(*)
                        FROM resetPasswordTokens
                        WHERE userid = " . $userid);
        $row = pg_fetch_array($result, 0);
        $alreadyPending = $row[0];

        if ($alreadyPending == 0) {
            $result = pg_query($link,
                                "INSERT INTO resetPasswordTokens VALUES (" .
                                $userid . ", '" . $token . "')");
        } else {
            $result = pg_query($link,
                            "UPDATE resetPasswordTokens
                            SET token = '" . $token .
                            "' WHERE userid = " . $userid);
        }

        if ($result) {
            $JSONobj->resetLinkSent = true;

            $confirmationLink = "http://students.mimuw.edu.pl/~kr394714/buwing/set_new_password.php?userid="
                                    . $userid . "&token=" . $token;

            $subject = "Resetowanie hasła w BUWing";

            $headers = "From: noreply@buwing.com\r\n";
            $headers .= "MIME-Version: 1.0\r\n";
            $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

            $message = '<p>' . $name . ' ' . $surname . ', odnotowaliśmy próbę zresetowania hasła do konta w BUWing.</p>
                        <p>Aby zresetować hasło, kliknij w ten <a href="' . $confirmationLink . '">link</a></p>';

            mail($email, $subject, $message, $headers);
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>