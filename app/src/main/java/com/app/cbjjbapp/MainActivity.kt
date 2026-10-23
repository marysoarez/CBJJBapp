package com.app.cbjjbapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.cbjjbapp.data.CertificateItem
import com.app.cbjjbapp.data.UserProfile
import com.app.cbjjbapp.ui.CertificateViewModel
import com.app.cbjjbapp.ui.LoginViewModel
import com.app.cbjjbapp.ui.ProfileViewModel
import com.app.cbjjbapp.ui.RegistrationViewModel
import com.app.cbjjbapp.ui.UiState
import com.app.cbjjbapp.util.ImageBase64Codec
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { CBJJBApp() }
    }
}

private object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PROFILE = "profile"
    const val CERTIFICATES = "certificates"
}

@Composable
private fun CBJJBApp() {
    MaterialTheme {
        val navController = rememberNavController()
        NavHost(navController, startDestination = Routes.LOGIN) {
            composable(Routes.LOGIN) { LoginScreen(navController) }
            composable(Routes.REGISTER) { RegistrationScreen(navController) }
            composable(Routes.PROFILE) { ProfileScreen(navController) }
            composable(Routes.CERTIFICATES) { CertificatesScreen(navController) }
        }
    }
}

@Composable
private fun LoginScreen(navController: NavHostController, viewModel: LoginViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(Unit) { if (viewModel.isSignedIn) navController.openProfile() }
    LaunchedEffect(state) {
        when (val current = state) {
            is UiState.Success -> navController.openProfile()
            is UiState.Error -> snackbar.showSnackbar(current.message)
            else -> Unit
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text("CBJJB", fontSize = 36.sp)
            Text("Carteirinha Digital", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(28.dp))
            OutlinedTextField(email, { email = it }, Modifier.fillMaxWidth(), label = { Text("E-mail") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), singleLine = true)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(password, { password = it }, Modifier.fillMaxWidth(), label = { Text("Senha") }, visualTransformation = PasswordVisualTransformation(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password), singleLine = true)
            Spacer(Modifier.height(20.dp))
            Button(onClick = { viewModel.signIn(email, password) }, enabled = state !is UiState.Loading, modifier = Modifier.fillMaxWidth()) {
                if (state is UiState.Loading) CircularProgressIndicator(Modifier.size(20.dp)) else Text("Entrar")
            }
            TextButton(onClick = { navController.navigate(Routes.REGISTER) }) { Text("Criar cadastro") }
        }
    }
}

@Composable
private fun RegistrationScreen(navController: NavHostController, viewModel: RegistrationViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    var nome by remember { mutableStateOf("") }; var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }; var doc by remember { mutableStateOf("") }
    var equipe by remember { mutableStateOf("") }; var tecnico by remember { mutableStateOf("") }
    var nascimento by remember { mutableStateOf("") }; var genero by remember { mutableStateOf("") }
    var peso by remember { mutableStateOf("") }; var telefone by remember { mutableStateOf("") }
    var faixa by remember { mutableStateOf("") }; var graduacao by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        when (val current = state) {
            is UiState.Success -> navController.openProfile()
            is UiState.Error -> snackbar.showSnackbar(current.message)
            else -> Unit
        }
    }
    Scaffold(topBar = { AppBar("Cadastro") { navController.popBackStack() } }, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp).verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FormField("Nome", nome) { nome = it }; FormField("E-mail", email, KeyboardType.Email) { email = it }
            FormField("Senha", senha, KeyboardType.Password, true) { senha = it }; FormField("Documento (opcional)", doc) { doc = it }
            FormField("Equipe", equipe) { equipe = it }; FormField("Professor ou técnico (opcional)", tecnico) { tecnico = it }
            FormField("Nascimento", nascimento) { nascimento = it }; FormField("Gênero", genero) { genero = it }
            FormField("Peso", peso, KeyboardType.Decimal) { peso = it }; FormField("Telefone", telefone, KeyboardType.Phone) { telefone = it }
            FormField("Faixa", faixa) { faixa = it }; FormField("Graduação", graduacao) { graduacao = it }
            Button(
                onClick = { viewModel.register(UserProfile(nome = nome, email = email, doc = doc, equipe = equipe, tecnico = tecnico, nascimento = nascimento, genero = genero, peso = peso, telefone = telefone, faixa = faixa, graduacao = graduacao, appsCarteira = setOf("cbjjb")), senha) },
                enabled = state !is UiState.Loading,
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            ) { Text(if (state is UiState.Loading) "Cadastrando..." else "Cadastrar") }
        }
    }
}

@Composable
private fun FormField(label: String, value: String, keyboardType: KeyboardType = KeyboardType.Text, password: Boolean = false, onChange: (String) -> Unit) {
    OutlinedTextField(value, onChange, Modifier.fillMaxWidth(), label = { Text(label) }, keyboardOptions = KeyboardOptions(keyboardType = keyboardType), visualTransformation = if (password) PasswordVisualTransformation() else VisualTransformation.None, singleLine = true)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(navController: NavHostController, viewModel: ProfileViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        viewModel.encodeAndSaveProfileImage(context.contentResolver, uri)
    }
    LaunchedEffect(Unit) { viewModel.load() }
    LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it) } }

    Scaffold(topBar = { TopAppBar(title = { Text("Carteirinha do atleta") }, actions = { TextButton(onClick = { viewModel.signOut(); navController.navigate(Routes.LOGIN) { popUpTo(0) } }) { Text("Sair") } }) }, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        val profile = state.profile
        Column(Modifier.fillMaxSize().padding(padding).padding(20.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
            if (state.loading) CircularProgressIndicator()
            val access = state.access
            Text(access?.message.orEmpty(), color = if (access?.allowed == true) Color(0xFF167A36) else MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(16.dp))
            if (access?.allowed == true && profile != null) {
                state.imageBase64?.let(ImageBase64Codec::decode)?.let { bitmap -> Image(bitmap.asImageBitmap(), "Foto do atleta", Modifier.size(180.dp).clip(CircleShape), contentScale = ContentScale.Crop) }
                TextButton(onClick = { picker.launch("image/*") }) { Text("Alterar foto") }
                ProfileRow("Nome", profile.nome); ProfileRow("Graduação", profile.graduacao); ProfileRow("Faixa", profile.faixa)
                ProfileRow("Peso", profile.peso); ProfileRow("Gênero", profile.genero); ProfileRow("Técnico", profile.tecnico); ProfileRow("Equipe", profile.equipe)
            }
            Spacer(Modifier.height(18.dp))
            Button(onClick = { navController.navigate(Routes.CERTIFICATES) }, modifier = Modifier.fillMaxWidth()) { Text("Certificados e ativação") }
        }
    }
}

@Composable
private fun ProfileRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) { Text(label, style = MaterialTheme.typography.labelLarge); Text(value) }
}

@Composable
private fun CertificatesScreen(navController: NavHostController, viewModel: CertificateViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbar = remember { SnackbarHostState() }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri ?: return@rememberLauncherForActivityResult
        viewModel.encodeAndSaveCertificate(context.contentResolver, uri)
    }
    LaunchedEffect(Unit) { viewModel.load() }
    LaunchedEffect(state.message) { state.message?.let { snackbar.showSnackbar(it) } }

    Scaffold(topBar = { AppBar("Certificados") { navController.popBackStack() } }, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { picker.launch("image/*") }, enabled = !state.loading, modifier = Modifier.weight(1f)) { Text("Adicionar") }
                Button(onClick = viewModel::requestActivation, enabled = !state.loading && state.certificates.isNotEmpty(), modifier = Modifier.weight(1f)) { Text("Solicitar ativação") }
            }
            Spacer(Modifier.height(12.dp))
            if (state.loading) CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            LazyVerticalGrid(columns = GridCells.Adaptive(150.dp), modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(state.certificates, key = CertificateItem::id) { certificate -> CertificateCard(certificate, viewModel::delete) }
            }
        }
    }
}

@Composable
private fun CertificateCard(certificate: CertificateItem, onDelete: (String) -> Unit) {
    Card {
        Column(Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            ImageBase64Codec.decode(certificate.imageBase64)?.let { bitmap -> Image(bitmap.asImageBitmap(), certificate.name, Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(8.dp)), contentScale = ContentScale.Crop) }
            Text(certificate.name, maxLines = 1)
            OutlinedButton(onClick = { onDelete(certificate.id) }) { Text("Excluir") }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(title: String, onBack: () -> Unit) {
    TopAppBar(title = { Text(title) }, navigationIcon = { TextButton(onClick = onBack) { Text("Voltar") } })
}

private fun NavHostController.openProfile() {
    navigate(Routes.PROFILE) { popUpTo(Routes.LOGIN) { inclusive = true } }
}
