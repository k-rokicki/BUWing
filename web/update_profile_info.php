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

    $login = $_POST["login"];
    $password = $_POST["password"];
    $newName = $_POST["newName"];
    $newSurname = $_POST["newSurname"];
    $newLogin = $_POST["newLogin"];
    $newEmail = $_POST["newEmail"];

    $JSONobj->updated = false;
    $JSONobj->name = "";
    $JSONobj->surname = "";
    $JSONobj->login = "";
    $JSONobj->email = "";

    $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
    $result = pg_query($link,
                        "SELECT password, email
                        FROM users
                        WHERE login = '" . pg_escape_string($login) . "'");
    $count = pg_num_rows($result);

    if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $hashedPassword = $row["password"];
        $oldEmail = $row["email"];
        if (password_verify($password, $hashedPassword)) {
            $activated = 1;
            if ($newEmail != $oldEmail) {
                $activated = 0;
            }
            
            $result = pg_query($link,
                        "UPDATE users
                        SET name = '" . pg_escape_string($newName) .
                        "', surname = '" . pg_escape_string($newSurname) .
                        "', login = '" . pg_escape_string($newLogin) .
                        "', email = '" . pg_escape_string($newEmail) .
                        "', activated = " . $activated .
                        " WHERE login = '" . pg_escape_string($login) .
                        "' RETURNING id, name, surname, login, email");
            $row = pg_fetch_array($result, 0);

            if ($result) {
                $JSONobj->updated = true;
                $JSONobj->name = $row["name"];
                $JSONobj->surname = $row["surname"];
                $JSONobj->login = $row["login"];
                $JSONobj->email = $row["email"];
                
                if ($activated == 0) {
                    $userid = $row["id"];
                    $token = randomString(50);

                    $result = pg_query($link,
                                        "INSERT INTO activationTokens VALUES ("
                                        . $userid . ", '"
                                        . $token .
                                        "')");

                    if ($result) {
                        $confirmationLink = "http://students.mimuw.edu.pl/~kr394714/buwing/activate.php?userid="
                                            . $userid . "&token=" . $token;

                        $subject = "Zmiana adresu email w BUWing";

                        $headers = "From: noreply@buwing.com\r\n";
                        $headers .= "MIME-Version: 1.0\r\n";
                        $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

                        $message = '<p>' . $newName . ' ' . $newSurname . ', zmieniono Twój adres email przypisany do konta w BUWing.</p>
                                    <p>Aktywuj konto klikając w ten <a href="' . $confirmationLink . '">link</a></p>';

                        mail($newEmail, $subject, $message, $headers);
                    }
                }
            }
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>