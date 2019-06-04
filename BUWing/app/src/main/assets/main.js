//window.scrollTo( 250, 250 );


// do sprawdzenia
var dt = new Date();
document.getElementById("datetime").innerHTML = dt.toLocaleTimeString();

var idStolik;
var initWindowHeight = window.innerHeight;
var initWindowWidth = window.innerWidth;
var login, password, floor, friend;





/* Funkcja wywoływana przy ładowaniu mapy */
function start() {

    getUserData();
    initTables();
    assignTableStatus();
}



/* Funkcja pobierająca i wypisująca dane użytownika */
function getUserData() {

    var url_string = window.location.href;
    var url = new URL(url_string);

    login = url.searchParams.get("login");
    password =  url.searchParams.get("password");
    floor = url.searchParams.get("floor");

    friend = url.searchParams.get("friend");
    if (friend != null) {
        //costam skrypt znajdujacy stolik (moze tez imie i nazwisko) i setfriends marker(stolik)
    }
}



/* Fukncja przypisująca stolikom id oraz inne atrybuty */
function initTables() {

    var tables = document.getElementsByClassName('stolik');

    for (let i = 0; i < tables.length; i++) {
        tables[i].setAttribute("height", "3");
        tables[i].setAttribute("width", "4.5");
        tables[i].setAttribute("id", i);
    }
}



/* Funkcja ustawijąca znacznik nad ławką o danym id */
function setFriendsMarker(tablesId) {
    var svg = document.getElementsByTagName('svg')[1];

    console.log(svg);

    var t = document.getElementById('10');
    var x = t.getAttribute('x');
    var y = t.getAttribute('y');
    svg.setAttribute("x", x);
    svg.setAttribute("y", y);
}



/* Obsługa kliknięć na poszczególne elementy ekranu */
document.body.onclick = function(e) {

    if (window.event)
        e = event.srcElement;
    else
       e = e.target;

    if (!e.getAttribute('class') || e.getAttribute('class').indexOf('popup') == -1)
        closePopups();

    if (e.getAttribute('class') && e.getAttribute('class').indexOf('stolik') != -1) {
        idStolik = e;

        //obsluga wolnego
        if (e.status == "wolny") {
            var postData = 'login=' + login + '&password=' + password;
            var http = new XMLHttpRequest();
            var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/seat_taken.php';
            http.open('POST', url, true);

            http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
            http.send(postData);

            http.onreadystatechange = function() {
                if (http.readyState == 4 && http.status == 200) {
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
        //okupowany to stolik ktory zajmuje uzytkownik
        else if (e.status == "okupowany") {
            showPopup(document.getElementById("popupZwolnij"));
        }
    }
}



/* Funkcja pokazująca okienka popup */
function showPopup(popup) {

    var windowHeight = window.innerHeight * 0.5;
    var windowWidth = window.innerWidth * 0.5;
    var font_size = window.innerHeight * 0.05;

    popup.style.height = windowHeight + "px";
    popup.style.width = windowWidth + "px";

    var marginLeft = window.scrollX + window.innerWidth/4;
    var marginTop = window.scrollY + window.innerHeight/4;
    var buttonTop = windowHeight*0.25;
    var buttonLeft = windowWidth/8;
    var buttonWidth = 6*windowWidth/8;
    var buttonHeight = buttonTop*2;

    popup.style.left = marginLeft + "px";
    popup.style.top = marginTop + "px";

    var popupButtons = document.getElementsByClassName('popupButton');
    for (let i = 0; i < popupButtons.length; i++) {
        popupButtons[i].style.fontSize = font_size + "px";
        popupButtons[i].style.top = buttonTop + "px";
        popupButtons[i].style.width = buttonWidth + "px";
        popupButtons[i].style.height = buttonHeight + "px";
    }

    document.getElementById('zwalnianie_przycisk').style.left = buttonLeft + "px";
    document.getElementById('zajmowanie_przycisk').style.left = buttonLeft + "px";

    popup.style.display = "block";
}



/* Funkcja zamykająca otwarte poupy */
function closePopups() {

    document.getElementById("popupZajmij").style.display = "none";
    document.getElementById("popupZwolnij").style.display = "none";
    document.getElementById("popupInfo").style.display = "none";
}



/* Funkcja zwalniająca zajmowany przez użytkownika stolik */
function freeTable() {

    var postData = 'login=' + login + '&password=' + password;
    var http = new XMLHttpRequest();
    var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/release_seat.php';
    http.open('POST', url, true);

    http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    http.send(postData);

    http.onreadystatechange = function() {
        if (http.readyState == 4 && http.status == 200) {
            const content = JSON.parse(http.responseText);

            var result =  content['released'];
            if (result && idStolik) {
                idStolik.setAttribute("fill", "#28724F");
                idStolik.status = "wolny";
            }
        }
    }

    closePopups();
}



/* Funkcja zajmująca wybrany przez użytkownika stolik */
function takeTable() {

    var table = idStolik.id;
    var postData = 'login=' + login + '&password=' + password + '&table=' + table + '&floor=' + floor;

    var http = new XMLHttpRequest();
    var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/take_seat.php';
    http.open('POST', url, true);

    http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    http.send(postData);

    http.onreadystatechange = function() {
        if (http.readyState == 4 && http.status == 200) {
            const content = JSON.parse(http.responseText);

            var result = content['took'];
            if (result == 1) {
                idStolik.setAttribute("fill", "orange");
                idStolik.status = "okupowany";
            }
        }
    }

    closePopups();
}



/* Funkcja przypisująca statusy stolikom na podstawie skryptu php przy zaladowaniu strony */
function assignTableStatus() {

    var postData = 'login=' + login + '&password=' + password + '&floor=' + floor;
    var http = new XMLHttpRequest();
    var url = 'http://students.mimuw.edu.pl/~af394182/tables_status_info.php';
    http.open('POST', url, true);

    http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
    http.send(postData);

    http.onreadystatechange = function() {
        if (http.readyState == 4 && http.status == 200) {
            const content = JSON.parse(http.responseText);
            var n = content['result'];
            var tables = content['table'];

            for (let i = 0; i < n; i++) {
                var table = document.getElementById(tables[i]["id"]);

                /*console.log(tables[i]["id"]);
                console.log(tables[i]["taken"]);
                console.log(tables[i]["login"]);*/

                if (table != null) {
                    if (tables[i]["taken"] == "t") {
                        if (tables[i]["login"] == login) {
                            table.status = "okupowany";
                            table.setAttribute("fill", "orange");
                        }
                        else {
                            table.status = "zajety";
                            table.setAttribute("fill", "#C86BA8");
                        }
                    }
                    else {
                        table.status = "wolny";
                        table.setAttribute("fill", "#28724F");
                    }
                }
            }
        }
    }
}