var GTMCode =  '';
var refNum = '';
var startDate = '';
var timesReturned = '';

if(document.getElementById('GTM')){
    GTMCode = document.getElementById('GTM').getAttribute('GTMCode');
    refNum = document.getElementById('GTM').getAttribute('ref');
    startDate = document.getElementById('GTM').getAttribute('startDate');
    timesReturned = document.getElementById('GTM').getAttribute('timesReturned');
}

// Google Tag Manager
(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push(
{'gtm.start': new Date().getTime(),event:'gtm.js'}
);var f=d.getElementsByTagName(s)[0], j=d.createElement(s),dl=l!=='dataLayer'?'&l='+l:'';j.async=true;j.src= 'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f); })(window,document,'script','dataLayer','GTM-'+GTMCode);
// End Google Tag Manager

// Track custom dimensions in GTM
window.dataLayer = window.dataLayer || [];

window.dataLayer.push({
    'event' : 'pageview',
    'dimension3': startDate,
    'dimension4': timesReturned,
    'dimension5': refNum
});
// End track custom dimensions in GTM
