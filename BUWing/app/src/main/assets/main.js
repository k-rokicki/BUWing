var idStolik;
var initWindowHeight = window.innerHeight;
var initWindowWidth = window.innerWidth;

// do sprawdzania ze przycisk odswieza
var dt = new Date();
document.getElementById("datetime").innerHTML = dt.toLocaleTimeString();


    document.body.onclick = function(e) {   //when the document body is clicked
        if (window.event) {
            e = event.srcElement;           //assign the element clicked to e (IE 6-8)
        }
        else {
           e = e.target;                   //assign the element clicked to e
        }

        if (!e.className || e.className.indexOf('popup') == -1) { //zamykanie popupow gdy kliknie sie gdzie indziej
            closePopups();
        }

        if (e.className && e.className.indexOf('stolik') != -1) {
            idStolik = e;
            if (e.status == "wolny") {  //obsluga wolnego
                showPopupTT(); 
            }
            else if (e.status == "okupowany") { //okupowany to stolik ktory zajmuje uzytkownik
                showPopupFT();
            }
        }
        var rect = e.getBoundingClientRect();
        console.log(rect.top, rect.right, rect.bottom, rect.left);
      //  console.log(window.scrollY);
      //  console.log(document.getElementById("stolik1").offsetLeft);
        
    }
    // pinch zoomowanie stolikow  
    document.getElementById("biblioteka").addEventListener('touchend', touchendeventListener, false);

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
    }
    
    

    function showPopupTT() {
        var windowHeight = window.innerHeight * 0.5; // zeby przy zoomie rozmiar popupu sie nie psul
        var windowWidth = window.innerWidth * 0.5;
        var font_size = window.innerHeight * 0.05;
        console.log(windowHeight);
        document.getElementById("popupZajmij").style.height = windowHeight + "px";
        document.getElementById("popupZajmij").style.width = windowWidth + "px";

        var marginLeft = window.scrollX + window.innerWidth/4;
        var marginTop = window.scrollY + window.innerHeight/4;

        document.getElementById("popupZajmij").style.left = marginLeft + "px";
        document.getElementById("popupZajmij").style.top = marginTop + "px";

        var popupButtons = document.getElementsByClassName('popupButton');
        popupButtons[0].style.fontSize = font_size + "px";
        /*for (let i = 0; i < popupButtons.length; i++) {
            popupButtons[i].style.fontSize = font_size + "px";
        }*/
        document.getElementById("popupZajmij").style.display = "block";
    }

    function showPopupFT() {
        var windowHeight = window.innerHeight * 0.5;
        var windowWidth = window.innerWidth * 0.5;
        var font_size = window.innerHeight * 0.05;
        document.getElementById("popupZwolnij").style.height = windowHeight + "px";
        document.getElementById("popupZwolnij").style.width = windowWidth + "px";

        var marginLeft = window.scrollX + window.innerWidth/4;
        var marginTop = window.scrollY + window.innerHeight/4;

        document.getElementById("popupZwolnij").style.left = marginLeft + "px";
        document.getElementById("popupZwolnij").style.top = marginTop + "px";
        var popupButtons = document.getElementsByClassName('popupButton');
        popupButtons[1].style.fontSize = font_size + "px";
        /*for (let i = 0; i < popupButtons.length; i++) {
            popupButtons[i].style.fontSize = font_size + "px";
        }*/
        document.getElementById("popupZwolnij").style.display = "block";
    }

    function closePopups() {
        document.getElementById("popupZajmij").style.display = "none";
        document.getElementById("popupZwolnij").style.display = "none";
    }

    function freeTable() {
        if (successfulFT()) {
            idStolik.style.background = "#28724F";
            idStolik.status = "wolny";
        }
        closePopups();
    }

    // zajmowanie stolika
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
       // console.log(postData);       
        var http = new XMLHttpRequest();
        var url = 'http://students.mimuw.edu.pl/~kr394714/buwing/take_seat.php';
        http.open('POST', url, true);

        http.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        http.send(postData);

        http.onreadystatechange = function() {
            if(http.readyState == 4 && http.status == 200) {
                const content = JSON.parse(http.responseText);
               // console.log(content['took']);
                var result = content['took'];
                if (result == 1) {
                    idStolik.style.background = "orange";
                    idStolik.status = "okupowany";
                }
            }
        }
        closePopups();
    }

    //laczenie z baza czy udalo sie zwolnic
    function successfulFT() {
        return true;
    }

    var login, password, floor;

    //przypisze statusy stolikom na podstawie skryptu php przy zaladowaniu strony
    function assignTableStatus() {
        document.getElementById("stolik1").status = "wolny";
        document.getElementById("stolik2").status = "wolny";

        document.getElementById("stolik3").status = "zajety";
        document.getElementById("stolik3").style.background = "#C86BA8";
    }


    function getUserData() {
        var url_string = window.location.href;
        var url = new URL(url_string);
        login = url.searchParams.get("login");
        password =  url.searchParams.get("password");
        floor = url.searchParams.get("floor");

        document.getElementById("dane").innerHTML = "dane uzytkownika (roboczo)" + login + password + floor;
    }

    function start() {
        assignTableStatus();
        getUserData();
    }