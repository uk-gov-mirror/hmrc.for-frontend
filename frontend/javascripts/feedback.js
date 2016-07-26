(function ($) {
    
    VoaFeedback.feedbackOverrides = function(){
        //remove if js
        $('.deleteifjs').remove();
        //feedback overrides
        $('.label--inlineRadio--overhead').addClass('block-label').removeClass('label--inlineRadio--overhead');
        $('.input--fullwidth').addClass('form-control').removeClass('input--fullwidth');
        $('#feedback-form fieldset fieldset legend').text(VoaMessages.textLabel('labelBetaFeedback'));
        $('#feedback-form fieldset small').text(VoaMessages.textLabel('labelBetaFeedback'));
        $('.form--feedback fieldset small').text(VoaMessages.textLabel('labelCommentLimit'));
        $('<strong class="feedbackLabel">'+VoaMessages.textLabel('labelBetaFeedbackImprove')+'</strong>').insertBefore($('label[for="feedback-name"]'));
        $('<p class="feedbackLabelFooter">'+VoaMessages.textLabel('labelBetaFeedbackDontInclude')+'</p>').insertAfter($('[for="feedback-comments"]'));
        $('<p class="feedbackLabelFooter">'+VoaMessages.textLabel('labelBetaFeedbackDontInclude')+'</p>').insertAfter($('[for="contact-comments"]'));
        $('#feedback-form [type="submit"]').text(VoaMessages.textLabel('labelBetaFeedbackButton'));
        $('[for="feedback-rating-5"]').html(VoaMessages.textLabel('labelBetaFeedback5')+'<input type="radio" id="feedback-rating-5" name="feedback-rating" value="5">');
        $('[for="feedback-rating-4"]').html(VoaMessages.textLabel('labelBetaFeedback4')+'<input type="radio" id="feedback-rating-4" name="feedback-rating" value="4">');
        $('[for="feedback-rating-3"]').html(VoaMessages.textLabel('labelBetaFeedback3')+'<input type="radio" id="feedback-rating-3" name="feedback-rating" value="3">');
        $('[for="feedback-rating-2"]').html(VoaMessages.textLabel('labelBetaFeedback2')+'<input type="radio" id="feedback-rating-2" name="feedback-rating" value="2">');
        $('[for="feedback-rating-1"]').html(VoaMessages.textLabel('labelBetaFeedback1')+'<input type="radio" id="feedback-rating-1" name="feedback-rating" value="1">');
        $('.form--feedback label[for="report-name"], .form--feedback label[for="feedback-name"] span:not(".form-field--error")').text(VoaMessages.textLabel('labelHelpName'));
        $('.form--feedback label[for="report-email"], .form--feedback label[for="feedback-email"] span:not(".form-field--error")').text(VoaMessages.textLabel('labelHelpEmail'));
        $('.form--feedback label[for="report-action"]').text(VoaMessages.textLabel('labelHelpAction'));
        $('.form--feedback label[for="report-error"]').text(VoaMessages.textLabel('labelHelpError'));
        $('.form--feedback input[id="report-name"]').attr('data-msg-required', VoaMessages.textLabel('errorHelpName'));
        $('.form--feedback input[id="report-email"]').attr('data-msg-required', VoaMessages.textLabel('errorHelpEmail'));
        $('.form--feedback input[id="report-action"]').attr('data-msg-required', VoaMessages.textLabel('errorHelpAction'));
        $('.form--feedback input[id="report-error"]').attr('data-msg-required', VoaMessages.textLabel('errorHelpError'));
        $('.form--feedback input[id="report-error"]').attr('data-msg-required', VoaMessages.textLabel('errorHelpError'));
        $('.form--feedback button[type="submit"]').text(VoaMessages.textLabel('buttonHelpSend'));
        $('.form--feedback label[for="feedback-name"] .error-notification').text(VoaMessages.textLabel('errorHelpName'));
        $('.form--feedback label[for="feedback-email"] .error-notification').text(VoaMessages.textLabel('errorHelpEmailInvalid'));
        $('.form--feedback label[for="feedback-comments"] .error-notification').text(VoaMessages.textLabel('errorHelpCommentsRequired'));
        $('.form--feedback fieldset.form-field--error span.error-notification').text(VoaMessages.textLabel('errorHelpRequired'));

        //vacate form
        $('.vacated-form [for="report-name"]').text(VoaMessages.textLabel('vacateFormName'));
        $('.vacated-form [for="report-email"]').text(VoaMessages.textLabel('vacateFormEmail'));
        $('.vacated-form [for="report-error"]').text(VoaMessages.textLabel('vacateFormGiveDetails'));

        $('.vacated-form [name="report-action"]').closest('div').show();
        $('.vacated-form label[for="report-action"]').addClass('visuallyhidden');
        $('.vacated-form [name="report-action"]').val('Ex-Owners/Occupiers or Vacated').attr('hidden', '');
        

        $('#report-error').each(function () {
            var   textbox = $(document.createElement('textarea'));
            $(this).replaceWith(textbox);
            textbox.attr('id', 'report-error')
                .attr('class', 'form-control')
                .attr('maxlength', '1000')
                .attr('name', 'report-error')
                .attr('data-rule-required','true')
                .attr('data-msg-required','Please enter details of what went wrong.');

        });

        var needle  =  $('.form--feedback label[for="feedback-comments"]').html();
        if(needle){
            $('.form--feedback label[for="feedback-comments"]').html(needle.replace(/Comments/g, VoaMessages.textLabel('labelFeedbackComments')));

        }
    };

    VoaFeedback.toggleHelp = function(){
        $('.form-help-toggle').click(function(e){
            e.preventDefault();
            $('#helpForm').toggle();
        });
    };

    VoaFeedback.helpForm = function() {
        $('#helpForm').on('submit', 'form', function(event) {
            event.preventDefault();
            $.ajax({
                type: 'POST',
                url:  $(this).attr('action'),
                data: $(this).serialize(),
                success: function(msg) {
                    console.log('Got success response: ' + msg);
                    $('.contact-form-copy').addClass('hidden');
                    $('.feedback-thankyou').removeClass('hidden');
                    $('.form--feedback').addClass('hidden');
                    $('#helpFormWrapper').addClass('hidden');
                },
                error: function(error) {
                    $('#helpFormWrapper').html('<p>'+error.responseText+'</p>');
                    $('.contact-form-copy').addClass('hidden');
                    $('.feedback-thankyou').removeClass('hidden');
                    $('.form--feedback').addClass('hidden');
                }
            });

        });
    };

})(jQuery);