var GTMCode =  '';
var startDim = '';
var startDate = '';
var returnDim = '';
var timesReturned = '';
var refDim = '';
var refNum = '';

if(document.getElementById('GTM')){
    GTMCode = document.getElementById('GTM').getAttribute('GTMCode');
    startDim = document.getElementById('GTM').getAttribute('startDim');
    startDate = document.getElementById('GTM').getAttribute('startDate');
    returnDim = document.getElementById('GTM').getAttribute('returnDim');
    timesReturned = document.getElementById('GTM').getAttribute('timesReturned');
    refDim = document.getElementById('GTM').getAttribute('refDim');
    refNum = document.getElementById('GTM').getAttribute('ref');
}

// Track custom dimensions in GTM
window.dataLayer = window.dataLayer || [];

window.dataLayer.push({startDim: startDate, returnDim: timesReturned, refDim: refNum, 'event': 'RALD01'});
// End track custom dimensions in GTM

// Google Tag Manager
(function(w,d,s,l,i){w[l]=w[l]||[];w[l].push(
{'gtm.start': new Date().getTime(),event:'gtm.js'}
);var f=d.getElementsByTagName(s)[0], j=d.createElement(s),dl=l!=='dataLayer'?'&l='+l:'';j.async=true;j.src= 'https://www.googletagmanager.com/gtm.js?id='+i+dl;f.parentNode.insertBefore(j,f); })(window,document,'script','dataLayer','GTM-'+GTMCode);
// End Google Tag Manager
