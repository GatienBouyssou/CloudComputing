const LOADER = "<div class=\"loader\"></div>";
const SELECTED = "selected";
const baseUrl = "api/appointment";
const urlAllAppointments = "/all";
const NBR_MIN_IN_HOUR = 60;

$(document).ready(() => {

    let todaysDate = getTodaysDate();

    $(".setTodaysDate").each(function(index){
       this.value = todaysDate;
    });

    //Click on an appointment 
    $("body").on("click", "tr", (e) => {
        if(e.currentTarget.classList.contains(SELECTED)) { //Appointment unselected
            e.currentTarget.classList.remove(SELECTED)
        } else { //appointment selected
        	
        	$(".selected").removeClass(SELECTED);
            e.currentTarget.classList.add(SELECTED);
            
            //set corresponding button visible
            $("#saveAppointmentButton").css("display", "block");
            $("#createAppointmentButton").css("display", "none");
            $("#modifyAndViewAppointment").modal({closeClass: 'close'}); // open modal
            
            //change opacity while fetching the data
            let formAppointment = $("#formCreateAppointment");
            formAppointment[0].style.opacity = 0.6;
            $.ajax({
                url: baseUrl + "/" + $(".selected")[0].id,
                type: 'GET',
                dataType: 'json',
                data: formAppointment.serialize(),
                success: function (appointment) {
                	formAppointment[0].style.opacity = 1;
                	console.log(appointment)
                	setAppointmentInForm("#formCreateAppointment", appointment)
                },
                error: function (err) {
                	formAppointment[0].style.opacity = 1;
                	console.log(err)
                	showSnackBar(err.responseText)
                }
            })
        }
    });

    // get appointments
    $('#submitFilters').click((e)=> {
    	e.preventDefault();
    	e.stopPropagation();
        if($("#formToGetAppointment")[0].reportValidity()){ /*If form is valid send to the server else let the user correct*/
            $("#formToGetAppointment").append(LOADER);
            $("#addAppointmentButton")[0].disabled = false;//enable adding appointments
            $.ajax({
                url: baseUrl + urlAllAppointments,
                type: 'get',
                dataType: 'json',
                data: $("#formToGetAppointment").serialize(),
                success: function (appointments) {
                    $(".loader").remove();
                    $(".listAppointments").empty();
                    if(appointments.length == 0) {
                    	showSnackBar("There is no appointment matching this query")
                    } else {
                    	appointments.forEach(function(appointment){
                    		$(".listAppointments").append(createTableRow(appointment));
                    	});
                    }
                    
                },
                error: function (err) {
                	$(".loader").remove();
                	console.log(err)
                	showSnackBar(err.responseText)
                }
            })
        }
    });


    /*Delete the selected elements*/
    $(document).on('keydown', function(e) {
        if(e.which === 46) {
        	deleteAppointmentSelected($(".selected")[0]);           
        }
    });

    /*Delete one element*/
    $("body").on("click", ".deleteButton", function(e) {
    	e.stopPropagation();
        deleteAppointmentSelected(e.currentTarget.parentNode.parentNode);
    });

    $('body').on("mouseenter", ".deleteButton", function (e) {
        this.classList.add("red");
    }).on("mouseleave", ".material-icons", function(e) {
        this.classList.remove("red");
    } );

    /*Modal events*/
    
    /*Modal appear*/
    $("#addAppointmentButton").click((e)=>{
    	e.preventDefault();
    	e.stopPropagation();
    	cleanInputs("formCreateAppointment");
    	//Enable button create and disable button save
    	$("#saveAppointmentButton").css("display", "none");
        $("#createAppointmentButton").css("display", "block");
        $("#modifyAndViewAppointment").modal({closeClass: 'close'});
    });
    
    /*Save an appointment*/
    $('#saveAppointmentButton').click(function (e) {
    	e.preventDefault();
    	e.stopPropagation();
        if($("#formCreateAppointment")[0].reportValidity()){ /*If form is valid send to the server else let the user correct*/
            $(".buttonRow").append(LOADER);
            $.modal.close();
            $.ajax({
                url: baseUrl + "/" + $(".selected")[0].id,
                type: 'PUT',
                method:'PUT',
                dataType: 'json',
                data: $('#formCreateAppointment').serializeArray(),
                success: function (appointment) {
                    $(".loader").remove();
                    $("#" + appointment.appointmentId).replaceWith(createTableRow(appointment))
                },
                error: function (err) {
                    $(".loader").remove();
                    showSnackBar(err.responseText)
                }
            })
        }
    });

    /*Create an appointment*/
    $('#createAppointmentButton').click(function (e) {
    	e.preventDefault();
    	e.stopPropagation();
        if($("#formCreateAppointment")[0].reportValidity()){ /*If form is valid send to the server else let the user correct*/
            $(".buttonRow").append(LOADER);
            $.modal.close();
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
	setTimeout(function(){$("#snackbar").removeClass("show"); }, 7000);
}

function createTableRow (appointment) {
	return "<tr id=\""+ appointment.appointmentId +"\">"+"<td>"+ appointment.title +"</td><td>" 
	+ appointment.owner + "</td><td>"+ buildDate(appointment.dateTime)
	+"</td><td class=\"icon\"><i class=\"material-icons deleteButton\">delete</i></td></tr>";
}

function buildDate(dateTime) {
	let date = new Date(dateTime);
	const month = date.toLocaleString('default', { month: 'long' });
	return date.getDate() + " " + month + " " + date.getFullYear() + ", " + date.getHours() + ":" + (date.getMinutes()<10?'0':'') + date.getMinutes();
}

function deleteAppointmentSelected(appointment) {
	appointment.style.opacity = 0.6;
    $.ajax({
        url: baseUrl + "/" + appointment.id,
        type: 'DELETE',
        method: 'DELETE',
        dataType: 'json',
        data: $('form#myForm').serialize(),
        success: function (response) {
            appointment.remove();
        },
        error: function (err) {
        	appointment.style.opacity = 1;
        	showSnackBar(err.responseText)
        }
    });
}

function setAppointmentInForm(appointmentForm, appointment) {
	$(appointmentForm + " [name=title]").val(appointment.title);
	$(appointmentForm + " [name=owner]").val(appointment.owner);
	
	let date = new Date(appointment.dateTime);
	$(appointmentForm + " [name=date]").val(date.toISOString().split("T")[0]);
	
	$(appointmentForm + " [name=hour]").val(date.getHours());
	$(appointmentForm + " [name=minutes]").val(date.getMinutes());
	
	$(appointmentForm + " [name=durationHour]").val(parseInt(appointment.durationInMinutes/NBR_MIN_IN_HOUR));
	$(appointmentForm + " [name=durationMin]").val(appointment.durationInMinutes%NBR_MIN_IN_HOUR);
	
	$(appointmentForm + " [name=description]").val(appointment.description);
}

function cleanInputs(formToCleanId) {
	$("#" + formToCleanId + " input").each(function(input) {
		this.value = "";
	})
}


