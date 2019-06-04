var idStolik;
var initWindowHeight = window.innerHeight;
var initWindowWidth = window.innerWidth;
var login, password, floor;
var prevTable, prevTableFloor;




/* Odświeżanie strony przy pomocyu przycisku */          // do sprawdzenia
var dt = new Date();
document.getElementById("datetime").innerHTML = dt.toLocaleTimeString();

    document.body.onclick = function(e) {
        if (window.event) {
            e = event.srcElement;
        }
        else {
           e = e.target;
        }

        if (!e.className || e.className.indexOf('popup') == -1) {
            closePopups();
        }

        if (e.className && e.className.indexOf('stolik') != -1) {
            idStolik = e;
            if (e.status == "wolny") {  //obsluga wolnego
                var postData = 'login=' + login + '&password=' + password;
                // console.log(postData);
                var http = new XMLHttpRequest();
                var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/seat_taken.php';
                http.open('POST', url, true);

                http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
                http.send(postData);

                http.onreadystatechange = function() {
                    if(http.readyState == 4 && http.status == 200) {
                        const content = JSON.parse(http.responseText);
                        var result = content['taken'];
                        if (!result) {
                            showPopup(document.getElementById("popupZajmij"));
                        }
                        else {
                            idStolik = document.getElementById(content['seatId']);
                            showPopup(document.getElementById("popupInfo"));
                        }
                    }
                }
            }
            else if (e.status == "okupowany") { //okupowany to stolik ktory zajmuje uzytkownik
                showPopup(document.getElementById("popupZwolnij"));
            }
        }
        //var rect = e.getBoundingClientRect();
       // console.log(rect.top, rect.right, rect.bottom, rect.left);
      //  console.log(window.scrollY);
      //  console.log(document.getElementById("stolik1").offsetLeft);
    }


    /* Funkcja odpowiedzialna za pinch zoomowanie stolików */
    /*document.getElementById("biblioteka").addEventListener('touchend', touchendeventListener, false);

    function touchendeventListener(event) {
        var tables = document.getElementsByClassName('stolik');
            if (window.innerHeight < initWindowHeight * 0.7) { //na razie na poziomie 70%
                for (let i = 0; i < tables.length; i++) {
                    tables[i].style.display = "block";
                }
            }
            else {
                for (let i = 0; i < tables.length; i++) {
                    tables[i].style.display = "none";
                }
            }
    }*/


    /* Funkcja pokazująca okienka popup */
    function showPopup(popup) {
        var windowHeight = window.innerHeight * 0.5;
        var windowWidth = window.innerWidth * 0.5;
        var font_size = window.innerHeight * 0.05;

        popup.style.height = windowHeight + "px";
        popup.style.width = windowWidth + "px";

        var marginLeft = window.scrollX + window.innerWidth/4;
        var marginTop = window.scrollY + window.innerHeight/4;

        popup.style.left = marginLeft + "px";
        popup.style.top = marginTop + "px";
        var popupButtons = document.getElementsByClassName('popupButton');
        for (let i = 0; i < popupButtons.length; i++) {
            popupButtons[i].style.fontSize = font_size + "px";
        }

        popup.style.display = "block";
    }


    /* Funkcja zamykająca otwarte poupy */
    function closePopups() {
        document.getElementById("popupZajmij").style.display = "none";
        document.getElementById("popupZwolnij").style.display = "none";
    }


    /* Funkcja zwalniająca zajmowany przez użytkownika stolik */
    function freeTable() {
        var postData = 'login=' + login + '&password=' + password;
       // console.log(postData);
        var http = new XMLHttpRequest();
        var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/release_seat.php';
        http.open('POST', url, true);

        http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        http.send(postData);

        http.onreadystatechange = function() {
            if (http.readyState == 4 && http.status == 200) {
                const content = JSON.parse(http.responseText);
                console.log(content);
                var result =  content['released'];
                if (result && idStolik) {
                    idStolik.style.background = "#28724F";
                    idStolik.status = "wolny";
                }
            }
        }
        closePopups();
    }


    /* Funkcja zajmująca wybrany przez użytkownika stolik */
    function takeTable() {
        var table;
        // roboczo bo nie ma zwalniania jeszcze
        if (idStolik.id == "stolik1") {
            table = "10";
        }
        else if (idStolik.id == "stolik2") {
            table = "11";
        }
        else {
            table = "12";
        }
        var postData = 'login=' + login + '&password=' + password + '&table=' + table + '&floor=' + floor;
        console.log(postData);
        var http = new XMLHttpRequest();
        var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/take_seat.php';
        http.open('POST', url, true);

        http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        http.send(postData);

        http.onreadystatechange = function() {
            if (http.readyState == 4 && http.status == 200) {
                const content = JSON.parse(http.responseText);
                console.log(content['took']);
                var result = content['took'];
                if (result == 1) {
                    idStolik.style.background = "orange";
                    idStolik.status = "okupowany";
                }
            }
        }
        closePopups();
    }


    /* Funkcja przypisująca statusy stolikom na podstawie skryptu php przy zaladowaniu strony */
    function assignTableStatus() {

        /*var postData = 'login=' + login + '&password=' + password + '&floor=' + floor;
        console.log(postData);
        var http = new XMLHttpRequest();
        //var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/tables_status_info.php';
        var url = 'tables_status_info.php';
        http.open('POST', url, true);

        http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        http.send(postData);

        http.onreadystatechange = function() {
            if (http.readyState == 4 && http.status == 200) {
                const content = JSON.parse(http.responseText);
                console.log(content['tables']);
                for (let i in content['tables']) {
                    var table = document.getElementById(i[0]);
                    if (table != null) {
                        if (i[1] == true) {
                            if (i[2] == login) {
                                table.status = "okupowany";
                                table.style.background = "orange";
                            }
                            else {
                                table.status = "zajety";
                                table.style.background = "#C86BA8";
                            }
                        }
                        else {
                            table.status = "wolny";
                            table.style.background = "#28724F";
                        }
                    }
                }
            }
        }*/

        document.getElementById("stolik1").status = "wolny";
        document.getElementById("stolik2").status = "wolny";

        document.getElementById("stolik3").status = "zajety";
        document.getElementById("stolik3").style.background = "#C86BA8";
    }


    /* Funkcja pobierająca i wypisująca dane użytownika */
    function getUserData() {
        var url_string = window.location.href;
        var url = new URL(url_string);
        login = url.searchParams.get("login");
        password =  url.searchParams.get("password");
        floor = url.searchParams.get("floor");

        document.getElementById("dane").innerHTML = "dane uzytkownika (roboczo)" + login + password + floor;
    }


    /* Funkcja nadająca stolikom id przy załadowywaniu strony */
    function initTablesId() {
        var tables = document.getElementsByClassName('stolik');

        for (let i = 0; i < tables.length; i++) {
            tables[i].id = i;
        }

        console.log(document.getElementsByClassName('stolik')[2].id);
    }


    /* Funkcja wywoływana przy ładowaniu mapy */
    function start() {
        //initTablesId();
        assignTableStatus();
        getUserData();
    }