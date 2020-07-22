import keys from './config.js';

const NOTES_TEMPLATE_DOC_ID = '1XlcAy-vrleXBxJl5Qy_SxGUyTqcwdUIhyJI2BygpNEc';

const GAPI_CLIENT_URL = 'https://apis.google.com/js/client.js?onload=initGAPI';
const COPY_FILE_URL = 'https://www.googleapis.com/drive/v3/files/fileId/copy';
const GOOGLE_DOC_URL = 'https://docs.google.com/document/d/';
const REVOKE_TOKEN_URL = 'https://accounts.google.com/o/oauth2/revoke?token=';
const USER_INFO_URL = 'https://www.googleapis.com/oauth2/v1/userinfo?access_token=';
// TODO: Change WEBAPP_URL from local URL to deployed website URL
const WEBAPP_URL = 'https://8080-cacf7a03-5e11-4b90-94f2-e81659d32917.us-east1.cloudshell.dev';

const CLIENT_ID = encodeURIComponent(keys.CLIENT_ID);
const CLIENT_SECRET = encodeURIComponent(keys.CLIENT_SECRET);
const API_KEY = keys.API_KEY;
const REDIRECT_URI = 
    encodeURIComponent('https://' + chrome.runtime.id + '.chromiumapp.org');
const SCOPES = encodeURIComponent([
  'openid',
  'email',
  'profile',
  'https://www.googleapis.com/auth/userinfo.email',
  'https://www.googleapis.com/auth/drive',
  'https://www.googleapis.com/auth/documents'
].join(' '));

const DISCOVERY_DOCS = [
  'https://docs.googleapis.com/$discovery/rest?version=v1',
  'https://www.googleapis.com/discovery/v1/apis/drive/v3/rest'
];

let loggedIn = false;
let accessToken = '';

/**
 * Gets the script for Google APIs client library for browser-side
 * JavaScript (gapi), sets the button functions, and fills the input. 
 */
window.onload = function() {
  let scriptEl = document.createElement('script');
  document.head.appendChild(scriptEl);
  scriptEl.src = GAPI_CLIENT_URL;

  document.getElementById('generate-note-btn').onclick = generateNote;
  document.getElementById('logout-btn').onclick = logout;
  document.getElementById('login-btn').onclick = login;
  document.getElementById('doc-name-input').value = 'gNote ' + getDate();
}

/** Initializes gapi. */
window.initGAPI = function initGAPI() {
  gapi.client.init({
    apiKey: API_KEY,
    discoveryDocs: DISCOVERY_DOCS,
  }).then(login)
  .catch(error => console.log('Error:', error));
}

/**
 * Requests user login (if necessary) then sets gapi access token
 * and displays user info in popup.
 */
function login() {
  const url =
      'https://accounts.google.com/o/oauth2/auth' + 
      '?client_id=' + CLIENT_ID + 
      '&response_type=code' + 
      '&access_type=offline' +
      '&redirect_uri=' + REDIRECT_URI + 
      '&scope=' + SCOPES;
  chrome.identity.launchWebAuthFlow({
    url: url,
    interactive: true
  }, redirectedTo => {
    if(chrome.runtime.lastError) {
      return;
    } else {
      // TODO: Make a POST request to user sign in servlet
      const response = redirectedTo.split('?code=', 2)[1];
      const code = response.split('&scope', 1)[0];
      const newUrl =
          'https://oauth2.googleapis.com/token' +
          '?grant_type=authorization_code' + 
          '&code=' + code + 
          '&redirect_uri=' + REDIRECT_URI + 
          '&client_id=' + CLIENT_ID + 
          '&client_secret=' + CLIENT_SECRET;
      getTokenEndpoint(newUrl)
        .then(tokenInfo => {
          accessToken = tokenInfo.access_token;
          registerUserOnWebapp(tokenInfo.id_token);
        })
    }
  })
}

/** Retrieves access token information for user from the url. */
function getTokenEndpoint(url) {
  return fetch(url, {
    method: 'POST'
  }).then(response => response.json());
}

/** Sends request to webapp's user registration servlet. */
function registerUserOnWebapp(idToken) {
  fetch(WEBAPP_URL + '/user-registration?idToken=' + idToken, {
    method: 'POST'
  }).then(() => {
    gapi.auth.setToken({ access_token: accessToken });
    loggedIn = true;
    setAccountInfo();
  });
}

/** Returns formatted string of today's date. */
function getDate() {
  const today = new Date();
  return today.toLocaleDateString('en-US');
}

/**
 * Generates a copy of the notes template and opens a new tab
 * with the newly created Google Doc.
 */
function generateNote() {
  let loadingIcon = document.getElementById('loading');
  loadingIcon.style.display = 'flex';
  let docName = document.getElementById('doc-name-input').value.trim();
  if(docName === '') {
    docName = 'gNote ' + getDate();
  }

  gapi.client.request({
    path: COPY_FILE_URL,
    method: 'POST',
    params: {fileId: NOTES_TEMPLATE_DOC_ID},
    body: {
      name: docName,
    }
  }).then(response => {
    const docId = response.result.id;
    const gNoteUrl = GOOGLE_DOC_URL + docId;
    compileNoteData(docName, docId, gNoteUrl);
    loadingIcon.style.display = 'none';
    chrome.tabs.create({ url: gNoteUrl });
  }).catch(error => {
    loadingIcon.style.display = 'none';
    console.log('Error:', error);
  })
}

/** Puts together the URL search params for posting the note */
async function compileNoteData(title, docId, docUrl) {
  const pdfResponse = await getPDFLink(docId);
  const pdfSource = pdfResponse.result.exportLinks['application/pdf'];
  const payload = {
    title: title,
    school: 'Unaffiliated',
    course: 'Unaffiliated',
    sourceUrl: docUrl,
    pdfSource: pdfSource
  }
  const noteData = new URLSearchParams(payload);
  postNoteToDatabase(noteData);
}

/** Retrieves PDF export link of Google Doc */
function getPDFLink(docId) {
  return gapi.client.drive.files.get({
    fileId: docId,
    fields: 'exportLinks'
  });
}

/**
 * Makes a request to the webapp's upload gnote servlet to add 
 * the note to the database.
 */
function postNoteToDatabase(noteData) {
  fetch(WEBAPP_URL + '/upload-gnote', {
    method: 'POST',
    body: noteData
  })
}

/**
 * Logs out the user by revoking their authentication token and making a
 * request to the webapp.
 */
function logout() {
  fetch(REVOKE_TOKEN_URL + accessToken)
    .then(logoutUserOnWebapp);
}

/**
 * Sends request to webapp's user logout servlet and resets the gapi
 * auth token.
 */
function logoutUserOnWebapp() {
  fetch(WEBAPP_URL + '/user-logout', {
    method: 'POST'
  }).then(() => {
    accessToken = '';
    gapi.auth.setToken({ access_token: accessToken });
    loggedIn = false;
    setAccountInfo();
  })
}

/**
 * If the user is logged in, displays their email and logout button, and
 * enables the generate note button. Otherwise, displays a login button 
 * and disables the generate note button.
 */
function setAccountInfo() {
  if(loggedIn) {
    gapi.client.request({
      path: USER_INFO_URL + accessToken
    }).then(response => {
      let userInfo = JSON.parse(response.body);
      document.getElementById('logout-btn').style.display = 'inline';
      document.getElementById('email').textContent = userInfo.email;
      document.getElementById('user-info').style.display = 'block';
      document.getElementById('login-btn').style.display = 'none';
      document.getElementById('generate-note-btn').disabled = false;
    });
  } else {
    document.getElementById('logout-btn').style.display = 'none';
    document.getElementById('email').textContent = '';
    document.getElementById('user-info').style.display = 'none';
    document.getElementById('login-btn').style.display = 'inline';
    document.getElementById('generate-note-btn').disabled = true;
  }
}