(function ($) {

    VoaFeedback.toggleHelp = function(){
        $('.form-help-toggle').click(function(e){
            e.preventDefault();
            $('#helpForm').toggle();
        });
    };

    VoaFeedback.helpForm = function() {
        $('#helpForm').on('submit', 'form', function(event) {
            event.preventDefault();
            $('#report-submit').prop('disabled', true);
            $.ajax({
                type: 'POST',
                url:  $(this).attr('action'),
                data: VoaFeedback.prepareData($(this)),
                success: function(msg) {
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

    VoaFeedback.prepareData = function (_data) {
        var data = _data.serializeArray().reduce(function(obj, item) {
            obj[item.name] = item.value;
            return obj;
        }, {});

        var referenceNumber = $('div.for-id').attr('for-id');
        if(referenceNumber === undefined) {
            referenceNumber = 'not provided';
        }

        var replaceRegex = new RegExp('https?://');

        data['report-error'] = data['report-error'] + '\n\nRef : ' + referenceNumber + '\n' +
            (String(window.location.href).replace(replaceRegex, ''));
        data['referer'] = String(window.location.href);
        data['isJavascript'] = true;

        return data;
    };


})(jQuery);