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
import androidx.compose.material3.LargeTopAppBar
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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.ui.graphics.Color


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
            0 -> DetailsList(warehouse)
            1 -> AssembliesList(warehouse)
            2 -> MechanismsList(warehouse)
        }
    }
}

@Composable
fun TabButton(text: String, selected: Boolean, onClick: () -> Unit) {

    val bgColor = MaterialTheme.colorScheme.primaryContainer  // нежно голубой
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
fun DetailsList(warehouse: Warehouse) {
    Text("Деталі", fontSize = 20.sp)

    LazyColumn {
        if (warehouse.allDetails.isEmpty()) {
            item {
                Text("• Немає", modifier = Modifier.padding(start = 10.dp))
            }
        } else {
            items(warehouse.allDetails) { detail ->
                var expanded by remember { mutableStateOf(false) }

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
                    ) {
                        Text(detail.name, fontSize = 16.sp)
                        Text(
                            if (expanded) "−" else "+",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    if (expanded) {
                        Column(modifier = Modifier.padding(start = 26.dp)) {
                            Text("- Виробник: ${detail.manufacturer}")
                            Text("- Рік виготовлення: ${detail.year}")
                            Text("- Ціна: ${detail.price}")
                            Text("- Матеріал: ${detail.material}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AssembliesList(warehouse: Warehouse) {
    Text("Вузли", fontSize = 20.sp)

    LazyColumn {
        if (warehouse.allAssemblies.isEmpty()) {
            item {
                Text("• Немає", modifier = Modifier.padding(start = 10.dp))
            }
        } else {
            items(warehouse.allAssemblies) { assembly ->
                var expanded by remember { mutableStateOf(false) }

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
                    ) {
                        Text(assembly.name, fontSize = 16.sp)
                        Text(
                            if (expanded) "−" else "+",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    if (expanded) {
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
                    }
                }
            }
        }
    }
}


@Composable
fun MechanismsList(warehouse: Warehouse) {
    Text("Механізми", fontSize = 20.sp)

    LazyColumn {
        if (warehouse.allMechanisms.isEmpty()) {
            item {
                Text("• Немає", modifier = Modifier.padding(start = 10.dp))
            }
        } else {
            items(warehouse.allMechanisms) { mechanism ->
                var expanded by remember { mutableStateOf(false) }

                Column {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = !expanded }
                            .padding(start = 10.dp, top = 4.dp, bottom = 4.dp)
                    ) {
                        Text(mechanism.name, fontSize = 16.sp)
                        Text(
                            if (expanded) "−" else "+",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    if (expanded) {
                        Column(modifier = Modifier.padding(start = 26.dp)) {
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

    // --- для вузла: пошук деталей і вибір ---
    var searchQueryDetails by remember { mutableStateOf("") }
    var searchResultsDetails by remember { mutableStateOf(listOf<Detail>()) }
    var selectedDetails by remember { mutableStateOf(setOf<Detail>()) }

    // --- для механізму: пошук вузлів і вибір ---
    var searchQueryAssemblies by remember { mutableStateOf("") }
    var searchResultsAssemblies by remember { mutableStateOf(listOf<Assembly>()) }
    var selectedAssemblies by remember { mutableStateOf(setOf<Assembly>()) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)

    ) {
        Text("Додати", fontSize = 30.sp, modifier = Modifier.padding(8.dp))

        // Вибір типу
        if (category == null) {
            Text("Оберіть тип:", fontSize = 16.sp, modifier = Modifier.padding(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Деталь", "Вузол", "Механізм").forEach { type ->
                    Button(onClick = {
                        // при переході скидаємо попередні вибори
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

        } else {
            Text("Тип: $category", fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Назва") },
                modifier = Modifier.fillMaxWidth()
            )
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

            if (category == "Деталь") {
                OutlinedTextField(
                    value = material,
                    onValueChange = { material = it },
                    label = { Text("Матеріал") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // --- Блок для Вузла: пошук деталей + чекбокси ---
            if (category == "Вузол") {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Додайте деталі до вузла", fontSize = 16.sp, modifier = Modifier.fillMaxWidth())

                OutlinedTextField(
                    value = searchQueryDetails,
                    onValueChange = { searchQueryDetails = it },
                    label = { Text("Пошук деталей") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        searchResultsDetails = warehouse.allDetails.filter {
                            it.name.contains(searchQueryDetails, ignoreCase = true)
                        }
                    }) {
                        Text("Пошук деталей")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Вибрано: ${selectedDetails.size}", modifier = Modifier.align(Alignment.CenterVertically))
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (searchResultsDetails.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
                        searchResultsDetails.forEach { detail ->
                            val checked = selectedDetails.contains(detail)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { checkedNow ->
                                        selectedDetails = if (checkedNow) selectedDetails + detail else selectedDetails - detail
                                    }
                                )
                                Text(detail.name, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                } else {
                    Text("(Пошук нічого не знайшов або порожній запит)", modifier = Modifier.padding(8.dp))
                }
            }

            // --- Блок для Механізму: пошук вузлів + чекбокси ---
            if (category == "Механізм") {
                Spacer(modifier = Modifier.height(12.dp))
                Text("Додайте вузли до механізму", fontSize = 16.sp, modifier = Modifier.fillMaxWidth())

                OutlinedTextField(
                    value = searchQueryAssemblies,
                    onValueChange = { searchQueryAssemblies = it },
                    label = { Text("Пошук вузлів") },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {
                        searchResultsAssemblies = warehouse.allAssemblies.filter {
                            it.name.contains(searchQueryAssemblies, ignoreCase = true)
                        }
                    }) {
                        Text("Пошук вузлів")
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Вибрано: ${selectedAssemblies.size}", modifier = Modifier.align(Alignment.CenterVertically))
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (searchResultsAssemblies.isNotEmpty()) {
                    Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
                        searchResultsAssemblies.forEach { assembly ->
                            val checked = selectedAssemblies.contains(assembly)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = checked,
                                    onCheckedChange = { checkedNow ->
                                        selectedAssemblies = if (checkedNow) selectedAssemblies + assembly else selectedAssemblies - assembly
                                    }
                                )
                                Text(assembly.name, modifier = Modifier.padding(start = 8.dp))
                            }
                        }
                    }
                } else {
                    Text("(Пошук нічого не знайшов або порожній запит)", modifier = Modifier.padding(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Кнопки Додати / Назад
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    try {
                        val y = year.toIntOrNull() ?: 2024
                        val p = price.toDoubleOrNull() ?: 0.0
                        when (category) {
                            "Деталь" -> {
                                warehouse.buy(
                                    Detail(name, manufacturer, y, p, material)
                                )
                            }

                            "Вузол" -> {
                                // створюємо вузол із вибраних деталей
                                val newAssembly = Assembly(name, manufacturer, y, p, selectedDetails.toList())
                                warehouse.buy(newAssembly)

                                // видаляємо використані деталі зі складу
                                warehouse.allDetails.removeAll(selectedDetails.toSet())
                            }

                            "Механізм" -> {
                                // створюємо механізм із вибраних вузлів
                                val newMechanism = Mechanism(name, manufacturer, y, p, selectedAssemblies.toList())
                                warehouse.buy(newMechanism)

                                // видаляємо використані вузли зі складу
                                warehouse.allAssemblies.removeAll(selectedAssemblies.toSet())
                            }
                        }

                        // очищення стану після додавання
                        name = ""; manufacturer = ""; year = ""; price = ""; material = ""
                        searchQueryDetails = ""; searchResultsDetails = emptyList(); selectedDetails = emptySet()
                        searchQueryAssemblies = ""; searchResultsAssemblies = emptyList(); selectedAssemblies = emptySet()
                        category = null

                    } catch (e: Exception) {
                        println("Помилка: ${e.message}")
                    }
                }) {
                    Text("Додати")
                }

                Button(onClick = {
                    // повернутися до вибору типу
                    category = null
                    name = ""; manufacturer = ""; year = ""; price = ""; material = ""
                    searchQueryDetails = ""; searchResultsDetails = emptyList(); selectedDetails = emptySet()
                    searchQueryAssemblies = ""; searchResultsAssemblies = emptyList(); selectedAssemblies = emptySet()
                }) {
                    Text("Назад")
                }
            }
        }
    }
}


