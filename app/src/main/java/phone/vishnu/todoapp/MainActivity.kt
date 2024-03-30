package phone.vishnu.todoapp

import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateUtils
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.commandiron.wheel_picker_compose.WheelDateTimePicker
import com.commandiron.wheel_picker_compose.core.TimeFormat
import com.commandiron.wheel_picker_compose.core.WheelPickerDefaults
import kotlinx.coroutines.launch
import phone.vishnu.todoapp.application.Application
import phone.vishnu.todoapp.helper.AlarmHelper
import phone.vishnu.todoapp.model.Shelve
import phone.vishnu.todoapp.ui.theme.TODOAppTheme
import phone.vishnu.todoapp.viewmodel.MainViewModel
import phone.vishnu.todoapp.viewmodel.MainViewModelFactory
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory((application as Application).repository)
    }

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TODOAppTheme {

                var floatingActionButtonAlpha by remember { mutableStateOf(1f) }

                val bottomSheetState =
                    rememberBottomSheetState(initialValue = BottomSheetValue.Collapsed)
                val scaffoldState =
                    rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)
                val coroutineScope = rememberCoroutineScope()

                BottomSheetScaffold(

                    floatingActionButton = {
                        ExtendedFloatingActionButton(
                            modifier = Modifier.alpha(floatingActionButtonAlpha),
                            text = { Text(text = "Add New Task") },
                            onClick = {
                                coroutineScope.launch {
                                    bottomSheetState.expand()
                                }
                            },
                            icon = { Icon(Icons.Filled.Add, "AddNewTask") },
                            elevation = FloatingActionButtonDefaults.elevation(
                                defaultElevation = 16.dp
                            )
                        )

                    },
                    floatingActionButtonPosition = androidx.compose.material.FabPosition.Center,

                    scaffoldState = scaffoldState,

                    sheetContent = {

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {

                            Text(
                                text = "New Task",
                                fontSize = 24.sp,
                                modifier = Modifier
                                    .padding(
                                        top = 16.dp,
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 8.dp
                                    )
                                    .fillMaxWidth(),
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Start
                            )

                            var title by remember { mutableStateOf("") }
                            var desc by remember { mutableStateOf("") }

                            TextInputField(
                                "Title",
                                title,
                                { s -> title = s },
                                KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    capitalization = KeyboardCapitalization.Words,
                                    imeAction = ImeAction.Next
                                )
                            )

                            TextInputField(
                                "Description",
                                desc,
                                { s -> desc = s },
                                KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    capitalization = KeyboardCapitalization.Sentences,
                                    imeAction = ImeAction.Done
                                )
                            )

                            var dateTime by remember {
                                mutableStateOf<LocalDateTime>(LocalDateTime.now())
                            }

                            WheelDateTimePicker(
                                modifier = Modifier.fillMaxWidth(),
                                textColor = MaterialTheme.colorScheme.onBackground,
                                startDateTime = LocalDateTime.now(),
                                minDateTime = LocalDateTime.now(),
                                timeFormat = if (DateFormat.is24HourFormat(this@MainActivity)) TimeFormat.HOUR_24 else TimeFormat.AM_PM,
                                selectorProperties = WheelPickerDefaults.selectorProperties(
                                    enabled = true,
                                    color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.1f),
                                    border = BorderStroke(2.dp, MaterialTheme.colorScheme.surface)
                                )
                            ) { snappedDateTime ->
                                dateTime = snappedDateTime
                            }

                            var isError by remember {
                                mutableStateOf(false)
                            }

                            OutlinedButton(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .background(Color.Transparent)
                                    .fillMaxWidth(),
                                border = BorderStroke(
                                    1.dp,
                                    if (isError) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.surfaceTint
                                ),
                                onClick = {

                                    if (title.isEmpty() || desc.isEmpty()) {
                                        isError = true
                                        return@OutlinedButton
                                    }

                                    val shelve = Shelve(
                                        title,
                                        desc,
                                        dateTime
                                            .atZone(ZoneId.systemDefault())
                                            .toInstant().toEpochMilli()
                                    )

                                    viewModel.insert(shelve)

                                    coroutineScope.launch {
                                        bottomSheetState.collapse()
                                        AlarmHelper.setAlarm(applicationContext, shelve)
                                    }
                                },
                            ) {
                                Row {
                                    Icon(
                                        Icons.Outlined.CheckCircle,
                                        "Submit",
                                        tint = MaterialTheme.colorScheme.surfaceTint,
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                            .padding(8.dp),
                                    )

                                    Text(
                                        text = "Submit",
                                        color = MaterialTheme.colorScheme.surfaceTint,
                                        fontSize = 16.sp,
                                        modifier = Modifier
                                            .align(Alignment.CenterVertically)
                                    )
                                }
                            }

                            LaunchedEffect(bottomSheetState) {
                                snapshotFlow { bottomSheetState.isCollapsed }.collect { isCollapsed ->
                                    if (isCollapsed) {
                                        title = ""
                                        desc = ""
                                        dateTime = LocalDateTime.now()
                                    }
                                }
                            }
                        }

                        floatingActionButtonAlpha = if (bottomSheetState.isCollapsed) 1f else 0f

                    },
                    sheetPeekHeight = 0.dp,
                    sheetBackgroundColor = MaterialTheme.colorScheme.background,
                    sheetElevation = 16.dp,
                    sheetShape = RoundedCornerShape(32.dp, 32.dp, 0.dp, 0.dp)

                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = paddingValues.calculateBottomPadding())
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Layout()
                    }
                }
            }
        }
    }

    @Composable
    private fun TextInputField(
        hint: String,
        text: String,
        onValueChange: (String) -> Unit,
        keyboardOptions: KeyboardOptions,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .padding(
                    top = 16.dp,
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 8.dp
                )
                .fillMaxWidth(),
            value = text,
            onValueChange = onValueChange,
            label = {
                Text(
                    hint,
                    color = MaterialTheme.colorScheme.surfaceTint,
                    fontSize = 12.sp
                )
            },
            singleLine = true,
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 16.sp
            ),
            keyboardOptions = keyboardOptions,
            isError = text.isEmpty(),
        )
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    fun Layout() {
        Column(modifier = Modifier.fillMaxSize()) {

            val text = getGreetingMessage()

            Text(
                text = AnnotatedString(
                    text,
                    listOf(
                        AnnotatedString.Range(
                            SpanStyle(color = Color.Gray),
                            text.length - 5,
                            text.length
                        ),
                    )
                ),
                fontSize = 36.sp,
                modifier = Modifier.padding(
                    top = 16.dp,
                    start = 24.dp,
                    end = 16.dp,
                    bottom = 8.dp
                )
            )

//            ChipGroup(
//                listOf("Class", "Life", "Travel", "Reading", "Code"),
//            )

            Column(
                Modifier
                    .fillMaxSize()
                    .padding(
                        bottom = 0.dp,
                        start = 16.dp,
                        end = 16.dp,
                        top = 4.dp
                    )
                    .background(
                        MaterialTheme.colorScheme.surface,
                        RoundedCornerShape(32.dp, 32.dp, 0.dp, 0.dp)
                    )
                    .padding(all = 4.dp)
            ) {

                Text(
                    text = "Tasks",
                    fontSize = 28.sp,
                    modifier = Modifier.padding(
                        top = 16.dp,
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold
                )

                var shelveList by remember {
                    mutableStateOf(listOf<Shelve>())
                }

                viewModel.getAllShelves().observe(this@MainActivity) { words ->
                    words?.let {
                        shelveList = words
                    }
                }

                if (shelveList.isEmpty()) {
                    Spacer(modifier = Modifier.weight(1.0f))
                    Icon(
                        painter = painterResource(id = R.drawable.no_data),
                        "Submit",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(180.dp)
                            .padding(8.dp),
                    )
                    Text(
                        text = "Nothing here!",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.weight(1.0f))
                }

                LazyColumn(
                    state = rememberLazyListState()
                ) {
                    itemsIndexed(
                        items = shelveList,
                        key = { _, listItem -> listItem.id }) { _, shelve ->

                        val state = rememberDismissState(
                            confirmStateChange = {
                                viewModel.delete(shelve)
                                AlarmHelper.cancelAlarm(applicationContext, shelve)

                                true
                            }
                        )

                        SwipeToDismiss(
                            state = state,
                            background = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(all = 8.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Color.Red),
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier
                                            .align(Alignment.CenterStart)
                                            .padding(16.dp)
                                    )
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier
                                            .align(Alignment.CenterEnd)
                                            .padding(16.dp)
                                    )
                                }
                            },
                            dismissContent = {
                                CardView(shelve)
                            }
                        )
                    }
                }
            }
        }
    }

    // https://stackoverflow.com/a/52272932/9652621
    private fun getGreetingMessage(): String {
        return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> "Good Morning, Folks"
            in 12..16 -> "Good Afternoon, Folks"
            in 17..23 -> "Good Evening, Folks"
            else -> "Hey There, Folks"
        }
    }

    @Preview
    @Composable
    fun Preview() {
        TODOAppTheme {
            Surface {
                Layout()
            }
        }
    }

    @Composable
    fun CardView(shelve: Shelve) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            modifier = Modifier
                .padding(all = 8.dp)
                .fillMaxSize(),
            shape = RoundedCornerShape(16.dp),
        ) {

            Column(
                modifier = Modifier.padding(all = 8.dp)
            ) {
                Text(
                    shelve.title,
                    modifier = Modifier.padding(all = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = shelve.description,
                    modifier = Modifier.padding(all = 8.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.labelLarge,
                )

                Divider(
                    color = MaterialTheme.colorScheme.onBackground,
                    thickness = 1.dp,
                    modifier = Modifier.padding(all = 8.dp),
                )

                Row {
                    Text(
                        text = LocalDateTime.ofInstant(
                            Instant.ofEpochMilli(shelve.dateDue),
                            ZoneId.systemDefault()
                        ).format(
                            DateTimeFormatter.ofPattern(
                                if (DateFormat
                                        .is24HourFormat(this@MainActivity)
                                ) "HH:mm dd/MM"
                                else "K:mm a dd/MM"
                            )
                        ),
                        modifier = Modifier.padding(
                            top = 4.dp,
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 4.dp
                        ),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.labelLarge,
                    )

                    Spacer(modifier = Modifier.weight(1.0f))

                    Text(
                        text = DateUtils.getRelativeTimeSpanString(shelve.dateDue).toString(),
                        modifier = Modifier.padding(
                            bottom = 4.dp,
                            start = 8.dp,
                            end = 8.dp,
                            top = 4.dp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }

    @Composable
    fun ChipGroup(chipItems: List<String>) = run {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(
                top = 4.dp,
                start = 16.dp,
                end = 16.dp,
                bottom = 8.dp
            )
        ) {
            Surface(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth(),
                shadowElevation = 8.dp,
                shape = MaterialTheme.shapes.medium,
            ) {
                LazyRow(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surface,
                            RoundedCornerShape(16.dp)
                        )
                        .padding(
                            bottom = 2.dp,
                            start = 4.dp,
                            end = 4.dp,
                            top = 2.dp
                        )
                ) {
                    items(chipItems) {
                        Chip(
                            name = it,
                            onSelectionChanged = {

                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun Chip(
        name: String,
        onSelectionChanged: (String) -> Unit = {},
    ) {

        val isSelected = remember { mutableStateOf(false) }

        Surface(
            modifier = Modifier
                .padding(
                    bottom = 8.dp,
                    start = 4.dp,
                    end = 4.dp,
                    top = 8.dp
                ),
            shadowElevation = 0.dp,
            color = MaterialTheme.colorScheme.background,
            shape = MaterialTheme.shapes.medium,
        ) {
            Row(modifier = Modifier
                .toggleable(
                    value = true,
                    onValueChange = {
                        isSelected.value = !isSelected.value
                        onSelectionChanged(name)
                    }
                )
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isSelected.value) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                    fontWeight = if (isSelected.value) FontWeight.Black else FontWeight.Normal,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}