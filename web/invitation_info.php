<?php

  $ini = parse_ini_file("database_credentials.ini");

  $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);

  $login = $_POST["login"];
  $password = $_POST["password"];

  $JSONobj->result = 0;

  $result = pg_query($link, "SELECT id, password FROM users
                            WHERE login =  '" . pg_escape_string($login) . "'");

  if (pg_num_rows($result) == 1) {
    $row = pg_fetch_array($result, 0);
    $userId = $row["id"];
    $hashedPassword = $row["password"];

    if (password_verify($password, $hashedPassword)) {
      $result = pg_query($link, "SELECT inviterid FROM friends
                                WHERE inviteeid = '" . pg_escape_string($userId) . "'
                                AND status = 'f'");
      $count = pg_num_rows($result);

      for ($i = 0; $i < $count; $i++) {
        $row = pg_fetch_array($result, $i);
        $array[] = $row["inviterid"];
      }

      $ids = implode(', ', $array);

      $result = pg_query($link, "SELECT login FROM users
                                WHERE id IN ($ids)");

      $logins = array();

      while ($row = pg_fetch_row($result)) {
        $logins[] = $row[0];
      }

      $JSONobj->result = 1;
      $JSONobj->users = $logins;
    }
  }

  pg_close($link);
  $returnJSON = json_encode($JSONobj);
  echo $returnJSON;
?>
