package com.example.lab4

import com.example.lab4.data.DataStorage
import com.example.lab4.logic.Assembly
import com.example.lab4.logic.Detail
import com.example.lab4.logic.Mechanism
import com.example.lab4.logic.Warehouse
import com.example.lab4.logic.Product
import com.example.lab4.ui.theme.Lab4Theme
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab4Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    App()
                }
            }
        }
    }
}

@Composable
fun App() {
    var screen by remember { mutableStateOf("main") }
    var currentWarehouse by remember { mutableStateOf<Warehouse?>(null) }

    when (screen) {
        "main" -> MainScreen(
            loginClick = { screen = "login" },
            registerClick = { screen = "register" }
        )

        "login" -> LoginScreen(
            onBack = { screen = "main" },
            onLoginSuccess = {
                currentWarehouse = it
                screen = "menu"
            }
        )

        "register" -> RegisterScreen(
            onBack = { screen = "main" },
            onRegisterSuccess = {
                currentWarehouse = it
                screen = "menu"
            }
        )

        "menu" -> MenuScreen(
            currentWarehouse = currentWarehouse,
            onLogout = {
                currentWarehouse = null
                screen = "main"
            }
        )
    }
}

@Composable
fun MainScreen(loginClick: () -> Unit, registerClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = loginClick, modifier = Modifier.padding(4.dp)) {
            Text("Вхід", fontSize = 20.sp)
        }
        Button(onClick = registerClick, modifier = Modifier.padding(4.dp)) {
            Text("Реєстрація", fontSize = 20.sp)
        }
    }
}

@Composable
fun RegisterScreen(onBack: () -> Unit, onRegisterSuccess: (Warehouse) -> Unit) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) } // стан для підсвічування помилок

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Реєстрація", fontSize = 22.sp)

        // Логін
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логін*") },
            isError = showErrors && login.isBlank(),
        )
        if (showErrors && login.isBlank()) {
            Text(
                text = "Логін обов'язковий",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Пароль
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль*") },
            visualTransformation = PasswordVisualTransformation(),
            isError = showErrors && password.isBlank(),
        )
        if (showErrors && password.isBlank()) {
            Text(
                text = "Пароль обов'язковий",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка підтвердження
        Button(
            onClick = {
                showErrors = true // вмикаємо підсвічування
                if (login.isBlank() || password.isBlank()) {
                    return@Button
                }
                val exists = DataStorage.warehouses.keys.any { it.name == login }
                if (exists) {
                    message = "Такий логін уже існує!"
                } else {
                    val newWh = Warehouse(login)
                    DataStorage.warehouses[newWh] = password
                    onRegisterSuccess(newWh)
                }
            },
            modifier = Modifier
                .padding(8.dp)

        ) {
            Text("Підтвердити")
        }

        // Кнопка назад
        Button(
            onClick = onBack,
            modifier = Modifier
                .padding(8.dp)

        ) {
            Text("Назад")
        }

        // Загальне повідомлення
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}


@Composable
fun LoginScreen(onBack: () -> Unit, onLoginSuccess: (Warehouse) -> Unit) {
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) } // для підсвічування порожніх полів

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Вхід", fontSize = 22.sp)

        // Логін
        OutlinedTextField(
            value = login,
            onValueChange = { login = it },
            label = { Text("Логін*") },
            isError = showErrors && login.isBlank(),
            modifier = Modifier
        )
        if (showErrors && login.isBlank()) {
            Text(
                text = "Логін обов'язковий",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Пароль
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль*") },
            visualTransformation = PasswordVisualTransformation(),
            isError = showErrors && password.isBlank(),
            modifier = Modifier
        )
        if (showErrors && password.isBlank()) {
            Text(
                text = "Пароль обов'язковий",
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка входу
        Button(
            onClick = {
                showErrors = true // активуємо перевірку
                if (login.isBlank() || password.isBlank()) {
                    return@Button
                }

                val warehouse = DataStorage.warehouses.keys.find { it.name == login }
                if (warehouse != null && DataStorage.warehouses[warehouse] == password) {
                    onLoginSuccess(warehouse)
                } else {
                    message = "Неправильний логін або пароль!"
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Увійти")
        }

        // Кнопка "Назад"
        Button(
            onClick = onBack,
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Назад")
        }

        // Повідомлення під кнопками
        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(currentWarehouse: Warehouse?, onLogout: () -> Unit) {
    if (currentWarehouse == null) {
        Text("Помилка: склад не знайдено")
        return
    }

    var selectedTab by remember { mutableStateOf("Склад") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${currentWarehouse.name}") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Вийти",

                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,

                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
            ) {
                // --- СКЛАД ---
                NavigationBarItem(
                    selected = selectedTab == "Склад",
                    onClick = { selectedTab = "Склад" },
                    label = { Text("Склад") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Склад"
                        )
                    }
                )

                // --- ДОДАТИ ---
                NavigationBarItem(
                    selected = selectedTab == "Додати",
                    onClick = { selectedTab = "Додати" },
                    label = { Text("Додати") },
                    icon = {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Додати"
                        )
                    }
                )
            }


        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        ) {
            when (selectedTab) {
                "Склад" -> WarehouseTab(currentWarehouse)
                "Додати" -> AddTab(currentWarehouse)
            }
        }
    }
}

@Composable
fun WarehouseTab(warehouse: Warehouse) {

    var selectedTab by remember { mutableStateOf(0) } // 0 - Деталі, 1 - Вузли, 2 - Механізми

    Column(
        horizontalAlignment = Alignment.Start,
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            "Склад",
            fontSize = 30.sp,
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            textAlign = TextAlign.Center
        )

        // ==== Верхние вкладки ====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabButton("Деталі", selectedTab == 0) { selectedTab = 0 }
            TabButton("Вузли", selectedTab == 1) { selectedTab = 1 }
            TabButton("Механізми", selectedTab == 2) { selectedTab = 2 }
        }

        Spacer(Modifier.height(16.dp))

        // ==== Контент в зависимости от выбранной вкладки ====
        when (selectedTab) {
            0 -> DetailsList(
                warehouse,
                onDeleteDetail = { product ->
                    warehouse.sell(product)})
            1 -> AssembliesList(
                warehouse,
                onDeleteAssembly = {product ->
                    warehouse.sell(product)})
            2 -> MechanismsList(
                warehouse,
                onDeleteMechanism = { product ->
                    warehouse.sell(product)
                }
            )
        }
    }
}

@Composable
fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {

    val bgColor = MaterialTheme.colorScheme.primaryContainer
    val textColor = Color.Black

    if (selected) {
        // ==== Выбранная - Outlined ====
        OutlinedButton(
            onClick = onClick,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = textColor
            )
        ) {
            Text(text)
        }

    } else {
        // ==== Не выбранная ====
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = bgColor,
                contentColor = textColor
            )
        ) {
            Text(text)
        }
    }
}

@Composable
fun DetailsList(warehouse: Warehouse, onDeleteDetail: (Detail) -> Unit) {


    LazyColumn {
        if (warehouse.allDetails.isEmpty()) {
            item {
                Text("Жодної деталі не додано", modifier = Modifier.padding(start = 10.dp), fontSize = 15.sp)
            }
        } else {
            items(warehouse.allDetails) { detail ->
                var expanded by remember { mutableStateOf(false) }
                var isEditing by remember { mutableStateOf(false) }
                var showDeleteDialog by remember { mutableStateOf(false) }

                // поля редактирования
                var manufacturer by remember { mutableStateOf(detail.manufacturer) }
                var year by remember { mutableStateOf(detail.year.toString()) }
                var price by remember { mutableStateOf(detail.price.toString()) }
                var material by remember { mutableStateOf(detail.material) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {

                    // ==== Заголовок детали ====
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, top = 4.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row (modifier = Modifier.clickable { expanded = !expanded }){
                            Text(detail.name, fontSize = 20.sp)
                            Text(
                                if (expanded) " −" else " +",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }

                        // ==== Кнопка редактировать ====
                        IconButton(onClick = {
                            expanded = true
                            isEditing = !isEditing
                        }) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Редагувати"
                            )
                        }
                    }

                    // ==== Раскрытая часть ====
                    if (expanded) {

                        if (!isEditing) {
                            // ----- обычный режим (просмотр) -----
                            Column(modifier = Modifier.padding(start = 26.dp)) {
                                Text("- Виробник: ${detail.manufacturer}")
                                Text("- Рік виготовлення: ${detail.year}")
                                Text("- Ціна: ${detail.price}")
                                Text(
                                    "- Матеріал: ${
                                        detail.material.ifBlank { "не вказано" }
                                    }"
                                )
                            }

                        } else {
                            // ----- режим редактирования -----
                            Column(modifier = Modifier.padding(start = 26.dp)) {

                                OutlinedTextField(
                                    value = manufacturer,
                                    onValueChange = { manufacturer = it },
                                    label = { Text("Виробник") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = year,
                                    onValueChange = { year = it },
                                    label = { Text("Рік") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = price,
                                    onValueChange = { price = it },
                                    label = { Text("Ціна") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = material,
                                    onValueChange = { material = it },
                                    label = { Text("Матеріал") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Button(onClick = {
                                        detail.manufacturer = manufacturer
                                        detail.year = year.toIntOrNull() ?: detail.year
                                        detail.price = price.toDoubleOrNull() ?: detail.price
                                        detail.material = material

                                        isEditing = false
                                    }) {
                                        Text("Зберегти")
                                    }

                                    OutlinedButton(onClick = {
                                        // отмена: вернуть старые значения
                                        manufacturer = detail.manufacturer
                                        year = detail.year.toString()
                                        price = detail.price.toString()
                                        material = detail.material
                                        isEditing = false
                                    }) {
                                        Text("Скасувати")
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                Button(
                                    onClick = { showDeleteDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Видалити", color = Color.White)
                                }

                                // ===== Диалог подтверждения удаления =====
                                if (showDeleteDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showDeleteDialog = false },
                                        title = { Text("Видалення") },
                                        text = { Text("Ви дійсно хочете видалити '${detail.name}'?") },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                onDeleteDetail(detail)
                                                showDeleteDialog = false
                                            },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = Color.Red // колір тексту
                                                )
                                            ) {
                                                Text("Видалити")
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showDeleteDialog = false }) {
                                                Text("Скасувати")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssembliesList(warehouse: Warehouse, onDeleteAssembly: (Assembly) -> Unit) {


    LazyColumn {
        if (warehouse.allAssemblies.isEmpty()) {
            item {
                Text("Жодного вузла не додано", modifier = Modifier.padding(start = 10.dp), fontSize = 15.sp)
            }
        } else {
            items(warehouse.allAssemblies) { assembly ->

                var expanded by remember { mutableStateOf(false) }
                var isEditing by remember { mutableStateOf(false) }
                var showDeleteDialog by remember { mutableStateOf(false) }

                // локальные поля редактирования
                var manufacturer by remember { mutableStateOf(assembly.manufacturer) }
                var year by remember { mutableStateOf(assembly.year.toString()) }
                var price by remember { mutableStateOf(assembly.price.toString()) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {

                    // ===== Заголовок =====
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, top = 4.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.clickable { expanded = !expanded }
                        ) {
                            Text(assembly.name, fontSize = 20.sp)
                            Text(if (expanded) " −" else " +",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }

                        IconButton(onClick = {
                            expanded = true
                            isEditing = !isEditing
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit assembly")
                        }
                    }

                    // ===== Раскрытая часть =====
                    if (expanded) {

                        if (!isEditing) {
                            // Просмотр обычных данных
                            Column(modifier = Modifier.padding(start = 26.dp)) {
                                Text("- Виробник: ${assembly.manufacturer}")
                                Text("- Рік виготовлення: ${assembly.year}")
                                Text("- Ціна: ${assembly.price}")
                                Text(
                                    "- Деталі: ${
                                        if (assembly.details.isEmpty()) "не вказано"
                                        else assembly.details.joinToString(", ") { it.name }
                                    }"
                                )
                            }

                        } else {
                            // ===== Режим редактирования =====
                            Column(modifier = Modifier.padding(start = 26.dp)) {

                                OutlinedTextField(
                                    value = manufacturer,
                                    onValueChange = { manufacturer = it },
                                    label = { Text("Виробник") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = year,
                                    onValueChange = { year = it },
                                    label = { Text("Рік") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = price,
                                    onValueChange = { price = it },
                                    label = { Text("Ціна") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Button(onClick = {
                                        assembly.manufacturer = manufacturer
                                        assembly.year = year.toIntOrNull() ?: assembly.year
                                        assembly.price = price.toDoubleOrNull() ?: assembly.price
                                        isEditing = false
                                    }) {
                                        Text("Зберегти")
                                    }

                                    OutlinedButton(onClick = {
                                        manufacturer = assembly.manufacturer
                                        year = assembly.year.toString()
                                        price = assembly.price.toString()
                                        isEditing = false
                                    }) {
                                        Text("Скасувати")
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                Button(
                                    onClick = { showDeleteDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Видалити", color = Color.White)
                                }

                                // ===== Диалог подтверждения удаления =====
                                if (showDeleteDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showDeleteDialog = false },
                                        title = { Text("Видалення") },
                                        text = { Text("Ви дійсно хочете видалити '${assembly.name}'?") },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                onDeleteAssembly(assembly)
                                                showDeleteDialog = false
                                            },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = Color.Red // колір тексту
                                                )
                                            ) {
                                                Text("Видалити")
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showDeleteDialog = false }) {
                                                Text("Скасувати")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MechanismsList(
    warehouse: Warehouse,
    onDeleteMechanism: (Mechanism) -> Unit
) {

    LazyColumn {
        if (warehouse.allMechanisms.isEmpty()) {
            item {
                Text("Жодного механізму не додано", modifier = Modifier.padding(start = 10.dp), fontSize = 15.sp)
            }
        } else {
            items(warehouse.allMechanisms) { mechanism ->

                var expanded by remember { mutableStateOf(false) }
                var isEditing by remember { mutableStateOf(false) }
                var showDeleteDialog by remember { mutableStateOf(false) }

                var manufacturer by remember { mutableStateOf(mechanism.manufacturer) }
                var year by remember { mutableStateOf(mechanism.year.toString()) }
                var price by remember { mutableStateOf(mechanism.price.toString()) }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 10.dp, top = 4.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.clickable { expanded = !expanded }
                        ) {
                            Text(mechanism.name, fontSize = 20.sp)
                            Text(
                                if (expanded) " −" else " +",
                                fontSize = 20.sp,
                                modifier = Modifier.padding(start = 6.dp)
                            )
                        }

                        IconButton(onClick = {
                            expanded = true
                            isEditing = !isEditing
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit mechanism")
                        }
                    }

                    if (expanded) {
                        if (!isEditing) {

                            Column(modifier = Modifier.padding(start = 20.dp)) {
                                Text("- Виробник: ${mechanism.manufacturer}")
                                Text("- Рік виготовлення: ${mechanism.year}")
                                Text("- Ціна: ${mechanism.price}")
                                Text(
                                    "- Вузли: ${
                                        if (mechanism.assemblies.isEmpty()) "не вказано"
                                        else mechanism.assemblies.joinToString(", ") { it.name }
                                    }"
                                )
                            }

                        } else {
                            // ===== Режим редагування =====
                            Column(modifier = Modifier.padding(start = 26.dp)) {

                                OutlinedTextField(
                                    value = manufacturer,
                                    onValueChange = { manufacturer = it },
                                    label = { Text("Виробник") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = year,
                                    onValueChange = { year = it },
                                    label = { Text("Рік") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                OutlinedTextField(
                                    value = price,
                                    onValueChange = { price = it },
                                    label = { Text("Ціна") },
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    Button(onClick = {
                                        mechanism.manufacturer = manufacturer
                                        mechanism.year = year.toIntOrNull() ?: mechanism.year
                                        mechanism.price = price.toDoubleOrNull() ?: mechanism.price
                                        isEditing = false
                                    }) {
                                        Text("Зберегти")
                                    }

                                    OutlinedButton(onClick = {
                                        manufacturer = mechanism.manufacturer
                                        year = mechanism.year.toString()
                                        price = mechanism.price.toString()
                                        isEditing = false
                                    }) {
                                        Text("Скасувати")
                                    }
                                }

                                Spacer(Modifier.height(10.dp))

                                Button(
                                    onClick = { showDeleteDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Видалити", color = Color.White)
                                }

                                // ===== Диалог подтверждения удаления =====
                                if (showDeleteDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showDeleteDialog = false },
                                        title = { Text("Видалення") },
                                        text = { Text("Ви дійсно хочете видалити '${mechanism.name}'?") },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                onDeleteMechanism(mechanism)
                                                showDeleteDialog = false
                                            },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = Color.Red // колір тексту
                                                )
                                            ) {
                                                Text("Видалити")
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showDeleteDialog = false }) {
                                                Text("Скасувати")
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}





@Composable
fun AddTab(warehouse: Warehouse) {
    var category by remember { mutableStateOf<String?>(null) }
    var name by remember { mutableStateOf("") }
    var manufacturer by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var material by remember { mutableStateOf("") }
    var showErrors by remember { mutableStateOf(false) }

    var searchQueryDetails by remember { mutableStateOf("") }
    var searchResultsDetails by remember { mutableStateOf(listOf<Detail>()) }
    var selectedDetails by remember { mutableStateOf(setOf<Detail>()) }

    var searchQueryAssemblies by remember { mutableStateOf("") }
    var searchResultsAssemblies by remember { mutableStateOf(listOf<Assembly>()) }
    var selectedAssemblies by remember { mutableStateOf(setOf<Assembly>()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        item {
            Text("Додати", fontSize = 30.sp, modifier = Modifier.padding(8.dp))
        }

        // --- Вибір типу ---
        if (category == null) {
            item {
                Text("Оберіть тип:", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Деталь", "Вузол", "Механізм").forEach { type ->
                        Button(onClick = {
                            category = type
                            name = ""; manufacturer = ""; year = ""; price = ""; material = ""
                            searchQueryDetails = ""; searchResultsDetails = emptyList(); selectedDetails = emptySet()
                            searchQueryAssemblies = ""; searchResultsAssemblies = emptyList(); selectedAssemblies = emptySet()
                        }) {
                            Text(type)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

        } else {

            // ------------------- Базові поля -------------------
            item {
                Text("Тип: $category", fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Назва*") },
                    isError = showErrors && name.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (showErrors && name.isBlank()) {
                    Text("Це поле обов'язкове", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }

            item {
                OutlinedTextField(
                    value = manufacturer, onValueChange = { manufacturer = it },
                    label = { Text("Виробник*") },
                    isError = showErrors && manufacturer.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (showErrors && manufacturer.isBlank()) {
                    Text("Це поле обов'язкове", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }

            item {
                OutlinedTextField(
                    value = year, onValueChange = { year = it },
                    label = { Text("Рік*") },
                    isError = showErrors && year.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (showErrors && year.isBlank()) {
                    Text("Це поле обов'язкове", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }

            item {
                OutlinedTextField(
                    value = price, onValueChange = { price = it },
                    label = { Text("Ціна*") },
                    isError = showErrors && price.isBlank(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (showErrors && price.isBlank()) {
                    Text("Це поле обов'язкове", color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }
            }

            if (category == "Деталь") {
                item {
                    OutlinedTextField(
                        value = material,
                        onValueChange = { material = it },
                        label = { Text("Матеріал") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // ============= ВУЗОЛ =============
            if (category == "Вузол") {

                item {
                    OutlinedTextField(
                        value = searchQueryDetails,
                        onValueChange = { searchQueryDetails = it },
                        label = { Text("Додайте деталі до вузла") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            searchResultsDetails = warehouse.allDetails.filter {
                                it.name.contains(searchQueryDetails, ignoreCase = true)
                            }
                        }) { Text("Пошук деталей") }

                        Spacer(modifier = Modifier.weight(1f))
                        Text("Вибрано: ${selectedDetails.size}", modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }

                if (searchResultsDetails.isNotEmpty()) {
                    items(searchResultsDetails) { detail ->
                        val checked = selectedDetails.contains(detail)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { now ->
                                    selectedDetails = if (now) selectedDetails + detail else selectedDetails - detail
                                }
                            )
                            Text(detail.name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                } else {
                    item {
                        Text("(Пошук нічого не знайшов)", modifier = Modifier.padding(8.dp))
                    }
                }
            }

            // ============= МЕХАНІЗМ =============
            if (category == "Механізм") {

                item {
                    OutlinedTextField(
                        value = searchQueryAssemblies,
                        onValueChange = { searchQueryAssemblies = it },
                        label = { Text("Додайте вузли до механізму") },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    )
                }

                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            searchResultsAssemblies = warehouse.allAssemblies.filter {
                                it.name.contains(searchQueryAssemblies, ignoreCase = true)
                            }
                        }) { Text("Пошук вузлів") }

                        Spacer(modifier = Modifier.weight(1f))
                        Text("Вибрано: ${selectedAssemblies.size}", modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }

                if (searchResultsAssemblies.isNotEmpty()) {
                    items(searchResultsAssemblies) { assembly ->
                        val checked = selectedAssemblies.contains(assembly)
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = checked,
                                onCheckedChange = { now ->
                                    selectedAssemblies = if (now) selectedAssemblies + assembly else selectedAssemblies - assembly
                                }
                            )
                            Text(assembly.name, modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                } else {
                    item {
                        Text("(Пошук нічого не знайшов)", modifier = Modifier.padding(8.dp))
                    }
                }
            }

            // ---------- КНОПКИ ----------
            item {
                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        showErrors = true
                        if (name.isBlank() || manufacturer.isBlank() || year.isBlank() || price.isBlank()) return@Button

                        val y = year.toIntOrNull() ?: 2024
                        val p = price.toDoubleOrNull() ?: 0.0

                        when (category) {
                            "Деталь" -> warehouse.buy(Detail(name, manufacturer, y, p, material))
                            "Вузол" -> {
                                warehouse.buy(Assembly(name, manufacturer, y, p, selectedDetails.toList()))
                                warehouse.allDetails.removeAll(selectedDetails)
                            }
                            "Механізм" -> {
                                warehouse.buy(Mechanism(name, manufacturer, y, p, selectedAssemblies.toList()))
                                warehouse.allAssemblies.removeAll(selectedAssemblies)
                            }
                        }

                        category = null
                        name = ""; manufacturer = ""; year = ""; price = ""; material = ""
                        selectedDetails = emptySet(); selectedAssemblies = emptySet()
                        searchQueryDetails = ""; searchResultsDetails = emptyList()
                        searchQueryAssemblies = ""; searchResultsAssemblies = emptyList()
                        showErrors = false
                    }) {
                        Text("Додати")
                    }

                    Button(onClick = {
                        showErrors = false
                        category = null
                        name = ""; manufacturer = ""; year = ""; price = ""; material = ""
                        selectedDetails = emptySet()
                        selectedAssemblies = emptySet()
                        searchQueryDetails = ""; searchResultsDetails = emptyList()
                        searchQueryAssemblies = ""; searchResultsAssemblies = emptyList()
                    }) {
                        Text("Назад")
                    }
                }
            }
        }
    }
}
