<?php
    $ini = parse_ini_file("database_credentials.ini");

    $login = $_POST["login"];
    $password = $_POST["password"];

    $link = pg_connect("host=labdb dbname=bd user=" . $ini['db_user'] . " password=" . $ini['db_password']);
    $result = pg_query($link,
                        "SELECT id, password
                        FROM users
                        WHERE login = '" . pg_escape_string($login) . "'");
    $count = pg_num_rows($result);

    if ($count == 1) {
        $row = pg_fetch_array($result, 0);
        $userId = $row["id"];
        $hashedPassword = $row["password"];
        if (password_verify($password, $hashedPassword)) {
            $result = pg_query($link,
                        "SELECT DISTINCT floor
                        FROM tables
                        WHERE taken = FALSE
                        ORDER BY floor ASC");

            $floorsNum = pg_num_rows($result);
            
            $availableFloors = array();
            $availableTables = array();

            for ($i = 0; $i < $floorsNum; $i++) {
                $row = pg_fetch_array($result, $i);
                $floor = $row["floor"];

                array_push($availableFloors, $floor);

                $availableTablesAtFloor = array();

                $query = pg_query($link,
                        "SELECT id
                        FROM tables
                        WHERE taken = FALSE
                        AND floor = " . $floor . "
                        ORDER BY id ASC");

                $tablesNum = pg_num_rows($query);
                
                for ($j = 0; $j < $tablesNum; $j++) {
                    $queryRow = pg_fetch_array($query, $j);
                    array_push($availableTablesAtFloor, $queryRow["id"]);
                }

                $availableTables[$floor] = $availableTablesAtFloor;
            }

            $JSONobj->availableFloors = $availableFloors;
            $JSONobj->availableTables = $availableTables;
        }
    }

    pg_close($link);

    $returnJSON = json_encode($JSONobj);
    echo $returnJSON;
?>