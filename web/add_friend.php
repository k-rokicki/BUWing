<?php

  $friend_login = $_POST["friend_login"];
  $my_login = $_POST["my_login"];
  $my_name = $_POST["my_name"];
  $my_surname = $_POST["my_surname"];

  $JSONobj->status = "no_user";
  $JSONobj->sent = false;

  $link = pg_connect("host=labdb dbname=bd user=kr394714 password=xyz");
  $result = pg_query($link, "SELECT id FROM users WHERE login = '" . pg_escape_string($my_login) . "'");
  $row = pg_fetch_array($result, 0);
  $my_id = $row["id"];
  $result = pg_query($link,
                      "SELECT id, name, surname, email
                      FROM users
                      WHERE login = '" . pg_escape_string($friend_login) . "'");
  $count = pg_num_rows($result);

  if ($count == 1) {
    $row = pg_fetch_array($result, 0);
    $friend_id = $row["id"];
    $friend_name = $row["name"];
    $friend_surname = $row["surname"];
    $friend_email = $row["email"];
    $result = pg_query($link,
                        "SELECT inviterid, inviteeid, status
                        FROM friends
                        WHERE inviterid = '" . pg_escape_string($my_id) . "'
                        AND inviteeid = '" . pg_escape_string($friend_id) . "'");
    $count = pg_num_rows($result);

    if ($count == 1) {
      $row = pg_fetch_array($result, 0);
      $status = ($row["status"] == 't');
      if (!$status) {
        $JSONobj->status = "already_sent";
      }
      else {
        $JSONobj->status = "already_friends";
      }
    }
    else {
      $result = pg_query($link,
                          "SELECT inviterid, inviteeid, status
                          FROM friends
                          WHERE inviterid = '" . pg_escape_string($friend_id) . "'
                          AND inviteeid = '" . pg_escape_string($my_id) . "'");
      $count = pg_num_rows($result);

      if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $status = ($row["status"] == 't');
        if (!$status) {
          $JSONobj->status = "pending_invitation";
        }
        else {
          $JSONobj->status = "already_friends";
        }
      }
      else {
        $result = pg_query($link,
                    "INSERT INTO friends
                    VALUES ('" . pg_escape_string($my_id) . "'
                    , '" . pg_escape_string($friend_id) . "'
                    , default)");
        if ($result) {
          $JSONobj->sent = true;
          $subject = "Nowe zaproszenie do znajomych w BUWing";

          $headers = "From: noreply@buwing.com\r\n";
          $headers .= "MIME-Version: 1.0\r\n";
          $headers .= "Content-Type: text/html; charset=UTF-8\r\n";

          $message = '<p>' . $friend_name . ' ' . $friend_surname . ', użytkownik ' . $my_name . ' ' . $my_surname . ' wysłał Ci zaproszenie do znajomych w BUWing.</p>
                     <p>Potwierdź zaproszenie w aplikacji mobilnej.</p>';

          mail($friend_email, $subject, $message, $headers);
        }
      }
    }
  }

  pg_close($link);

  $returnJSON = json_encode($JSONobj);
  echo $returnJSON;
?>
