<?php

  header('Access-Control-Allow-Origin: *');
  header('Access-Control-Allow-Methods: GET, POST');
  header("Access-Control-Allow-Headers: X-Requested-With");
  
  $ini = parse_ini_file("database_credentials.ini");
  
  $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);
  
  $login = $_POST["login"];
  $password = $_POST["password"];
  $floor = $_POST["floor"];
  
  //$login = "alpaczka";
  //$password = "Alpasia22@";
  //$floor = 1;
  
  $JSONobj->result = 0;
  $result = pg_query($link, "SELECT id, password FROM users
                            WHERE login =  '" . pg_escape_string($login) . "'");

  if (pg_num_rows($result) == 1) {
    $row = pg_fetch_array($result, 0);
    $userId = $row["id"];
    $hashedPassword = $row["password"];
    
    if (password_verify($password, $hashedPassword)) {
      $result = pg_query($link,
                        "SELECT tables.id, taken, login 
                        FROM tables LEFT JOIN users ON tables.userid = users.id
                        WHERE floor = " . $floor . "");
                                
      $count = pg_num_rows($result);
      $tables = array();
      
      for ($i = 0; $i < $count; $i++) {
        $row = pg_fetch_array($result, $i, PGSQL_ASSOC);
        $tables[] = $row;
      }
      
      $JSONobj->result = $count;
      $JSONobj->table = $tables;
    }
  }
  
  pg_close($link);
  $returnJSON = json_encode($JSONobj);
  echo $returnJSON;
?>

