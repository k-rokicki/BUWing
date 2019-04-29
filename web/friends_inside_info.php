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
      $result = pg_query($link, "SELECT login, floor FROM tables LEFT JOIN users
                                ON tables.userid = users.id
                                WHERE userid = ANY(SELECT inviterid FROM friends
                                WHERE inviteeid = $userId AND status = 't' UNION
                                SELECT inviteeid FROM friends WHERE inviterid = $userId
                                AND status = 't') ORDER BY floor");
      $count = pg_num_rows($result);

      $logins = array();

      for ($i = 0; $i < $count; $i++) {
        $row = pg_fetch_array($result, $i, PGSQL_ASSOC);
        $logins[] = $row;
      }

      $JSONobj->result = 1;
      $JSONobj->friends = $logins;
    }
  }

  pg_close($link);
  $returnJSON = json_encode($JSONobj);
  echo $returnJSON;
?>
