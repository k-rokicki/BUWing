<?php

  header('Access-Control-Allow-Origin: *');
  header('Access-Control-Allow-Methods: GET, POST');
  header("Access-Control-Allow-Headers: X-Requested-With");
  
  $ini = parse_ini_file("database_credentials.ini");
  
  $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);
  
  $login = $_POST["login"];
  $password = $_POST["password"];
  $friend = $_POST["friend"];
  
  $JSONobj->result = 0;
  $result = pg_query($link, "SELECT id, password FROM users
                            WHERE login =  '" . pg_escape_string($login) . "'");

  if (pg_num_rows($result) == 1) {
    $row = pg_fetch_array($result, 0);
    $userId = $row["id"];
    $hashedPassword = $row["password"];
    
    if (password_verify($password, $hashedPassword)) {
      $friends_id = pg_query($link, "SELECT id FROM users
                                    WHERE login =  '" . pg_escape_string($friend) . "'");
                                    
      $id = pg_fetch_array($friends_id, 0);
      
      $tables_id = pg_query($link, "SELECT id FROM tables
                                    WHERE userid =  '" . $id[0] . "'");
    
      $table = pg_fetch_array($tables_id, 0);
    
      $JSONobj->tablesId = $table[0];
    }
  }
  
  pg_close($link);
  $returnJSON = json_encode($JSONobj);
  echo $returnJSON;
?>
