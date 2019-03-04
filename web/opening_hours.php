<?php
    $startHour;
    $endHour;

    $dayOfWeek = date("N");
    
    if ($dayOfWeek >= 1 && $dayOfWeek <= 5) {
        $startHour = "8:00";
        $endHour = "22:00";
    } elseif ($dayOfWeek == 6) {
        $startHour = "9:00";
        $endHour = "21:00";
    } elseif ($dayOfWeek == 7) {
        $startHour = "15:00";
        $endHour = "20:00";
    }

    echo $startHour . " - " . $endHour . "\n";
?>