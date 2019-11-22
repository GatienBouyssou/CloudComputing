const LOADER = "<div class=\"loader\"></div>";
const SELECTED = "selected";
const baseUrl = "api/appointment";
const urlAllAppointments = "/all";

$(document).ready(() => {

    let todaysDate = getTodaysDate();

    $(".setTodaysDate").each(function(index){
       this.value = todaysDate;
    });

    $("tr").click((e) => {
        if(e.currentTarget.classList.contains(SELECTED)) {
            e.currentTarget.classList.remove(SELECTED)
        } else {
        	$(".selected").removeClass("selected");
            e.currentTarget.classList.add(SELECTED);
        }
    });

    // get appointments
    $('#submitFilters').click((e)=> {
    	e.preventDefault();
    	e.stopPropagation();
        if($("#formToGetAppointment")[0].checkValidity()){ /*If form is valid send to the server else let the user correct*/
            $("#formToGetAppointment").append(LOADER);
            console.log($("#formToGetAppointment").serialize());
            $.ajax({
                url: baseUrl + urlAllAppointments,
                type: 'get',
                method:'GET',
                dataType: 'json',
                data: $("#formToGetAppointment").serialize(),
                success: function (appointments) {
                    $(".loader").remove();
                    $(".listAppointments").empty();
                    appointments.forEach(function(appointment){
                    	$(".listAppointments").append(createTableRow(appointment));
                    });
                },
                error: function (err) {
                	$(".loader").remove();
                	showSnackBar(err.responseText)
                }
            })
        }
    });


    /*Delete the selected elements*/
    $(document).on('keydown', function(e) {
        if(e.which === 46) {
            if (confirm('Do you want to delete these items ?')) {
                $.ajax({
                    url: baseUrl + urlAllAppointments ,
                    type: 'post',
                    method: 'DELETE',
                    dataType: 'json',
                    data: $('form#myForm').serialize(),
                    success: function (data) {
                        $('.selected').each(function (index) {
                            this.remove();
                        })
                    },
                    error: function (err) {
                        console.log("Sorry we couldn't delete those items")
                    }
                })
            }
        }
    });

    /*Delete one element*/
    $("body").on("click", ".deleteButton", function(e) {
        if (confirm('Are you sure you want to delete this item ?')) {
            $.ajax({
                url: 'some-url',
                type: 'post',
                dataType: 'json',
                data: $('form#myForm').serialize(),
                success: function (data) {
                    this.parentElement.parentElement.remove();
                },
                error: function (err) {
                    console.log("Sorry we couldn't delete this item")
                }
            })
        }
    });

    $('body').on("mouseenter", ".material-icons", function (e) {
        this.classList.add("red");
    }).on("mouseleave", ".material-icons", function(e) {
        this.classList.remove("red");
    } );

    /*Right panel events*/

    /*Save an appointment*/
    $('#saveAppointmentButton').click(function (e) {
    	e.preventDefault();
    	e.stopPropagation();
        if($("#formCreateAppointment")[0].checkValidity()){ /*If form is valid send to the server else let the user correct*/
            $(".buttonRow").append(LOADER);
            $.ajax({
                url: baseUrl,
                type: 'PUT',
                method:'PUT',
                dataType: 'json',
                data: $('#formCreateAppointment').serialize(),
                success: function (data) {
                    $(".loader").remove();
                    // ... do something with the data...
                },
                error: function (err) {
                    $(".loader").remove();
                    console.log("error not implemented yet")
                }
            })
        }
    });

    /*Create an appointment*/
    $('#createAppointmentButton').click(function (e) {
    	e.preventDefault();
    	e.stopPropagation();
        if($("#formCreateAppointment")[0].checkValidity()){ /*If form is valid send to the server else let the user correct*/
            $(".buttonRow").append(LOADER);
            console.log($('#formCreateAppointment').serializeArray())
            $.ajax({
                url: baseUrl,
                type: 'POST',
                method:'POST',
                dataType: 'json',
                data: $('#formCreateAppointment').serializeArray(),
                success: function (appointment) {
                    $(".loader").remove();
                    showSnackBar("Appointment created !")
                    $(".listAppointments").append(createTableRow(appointment));
                },
                error: function (err) {
                    $(".loader").remove();
                    console.log(err)
                    showSnackBar(err.responseText)
                }
            })
        }
    });
});


function getTodaysDate() {
    let today = new Date();
    let dd = String(today.getDate()).padStart(2, '0');
    let mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
    let yyyy = today.getFullYear();
    return yyyy + '-' + mm + '-' + dd;
}

function doubleTheDigit(value) {
    if(parseInt(value,10)<10)
        return '0'+value;
    return value;
}

function showSnackBar(message) {
	$("#snackbar")[0].innerText = message;
	$("#snackbar").addClass("show")
	setTimeout(function(){ $("#snackbar").removeClass("show"); }, 3000);
}

function createTableRow (appointment) {
	return "<tr id=\""+ appointment.appointmentId +"\">"+"<td>"+ appointment.title +"</td>" + appointment.owner + "<td></td><td>"+ new Date(appointment.dateTime) +"</td><td class=\"icon\"><i class=\"material-icons deleteButton\">delete</i></td></tr>"
}