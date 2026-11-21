package com.venturebridge.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Navy = Color(0xFF10243A)
private val Teal = Color(0xFF16A6A0)
private val Pale = Color(0xFFF3F6F8)
private val Ink = Color(0xFF1D2B3A)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { VentureBridgeApp() }
    }
}

data class Opportunity(val title: String, val company: String, val industry: String, val amount: String, val stage: String, val description: String)

private val sampleOpportunities = listOf(
    Opportunity("توسعه واکسن نوترکیب HIV", "زیست‌پژوهان نوآور", "زیست‌فناوری و دارو", "۴۰ میلیارد تومان", "پیش‌بالینی", "توسعه و تکمیل مطالعات پیش‌بالینی یک نامزد واکسن نوترکیب؛ اطلاعات این نمونه کاملاً فرضی است."),
    Opportunity("کیت تشخیص سریع بیماری‌های ویروسی", "پارس‌تشخیص", "تجهیزات پزشکی", "۱۲ میلیارد تومان", "نمونه اولیه", "توسعه خط تولید و اخذ مجوزهای لازم برای کیت تشخیص سریع."),
    Opportunity("سامانه ذخیره‌سازی انرژی", "توان‌آوران آینده", "انرژی", "۲۵ میلیارد تومان", "آماده تجاری‌سازی", "افزایش ظرفیت تولید سامانه ذخیره‌سازی انرژی برای مشتریان صنعتی.")
)

@Composable
fun VentureBridgeApp() {
    var selected by remember { mutableStateOf("خانه") }
    var selectedOpportunity by remember { mutableStateOf<Opportunity?>(null) }
    var search by remember { mutableStateOf("") }
    var companyName by remember { mutableStateOf("") }
    var projectName by remember { mutableStateOf("") }
    var industry by remember { mutableStateOf("زیست‌فناوری و دارو") }
    var amount by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }
    val opportunities = remember { mutableStateListOf<Opportunity>().apply { addAll(sampleOpportunities) } }
    val tabs = listOf("خانه", "فرصت‌ها", "شرکت‌ها", "سرمایه‌گذاران", "ارزیابی", "ثبت فرصت")

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(colorScheme = lightColorScheme(primary = Navy, secondary = Teal, background = Pale, surface = Color.White)) {
            Scaffold(
                containerColor = Pale,
                topBar = {
                    Column(Modifier.fillMaxWidth().background(Navy).padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("VentureBridge", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                                Text("پل ارتباطی فرصت‌ها و سرمایه", color = Color(0xFFB9D2DF), fontSize = 12.sp)
                            }
                            Surface(color = Color(0xFF24445C), shape = RoundedCornerShape(12.dp)) {
                                Text("نسخه آزمایشی", color = Color(0xFF8DE1D8), fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp))
                            }
                        }
                    }
                },
                bottomBar = {
                    NavigationBar(containerColor = Color.White) {
                        val visibleTabs = tabs.take(5)
                        visibleTabs.forEach { tab ->
                            val icon = when(tab) { "خانه" -> Icons.Default.Home; "فرصت‌ها" -> Icons.Default.Lightbulb; "شرکت‌ها" -> Icons.Default.Business; "سرمایه‌گذاران" -> Icons.Default.Groups; else -> Icons.Default.Assessment }
                            NavigationBarItem(selected = selected == tab, onClick = { selected = tab; selectedOpportunity = null }, icon = { Icon(icon, null) }, label = { Text(tab, fontSize = 10.sp) })
                        }
                    }
                },
                floatingActionButton = {
                    if (selected != "ثبت فرصت") FloatingActionButton(onClick = { selected = "ثبت فرصت" }, containerColor = Teal, contentColor = Color.White) { Icon(Icons.Default.Add, "ثبت فرصت") }
                }
            ) { padding ->
                Box(Modifier.padding(padding).fillMaxSize()) {
                    if (selectedOpportunity != null) {
                        OpportunityDetail(selectedOpportunity!!) { selectedOpportunity = null }
                    } else when (selected) {
                        "خانه" -> Dashboard(onNavigate = { selected = it }, opportunities = opportunities)
                        "فرصت‌ها" -> Opportunities(search, { search = it }, { selectedOpportunity = it }, opportunities)
                        "شرکت‌ها" -> Companies()
                        "سرمایه‌گذاران" -> Investors()
                        "ارزیابی" -> Assessment()
                        "ثبت فرصت" -> RegisterOpportunity(companyName, { companyName = it }, projectName, { projectName = it }, industry, { industry = it }, amount, { amount = it }, submitted, { submitted = it }) { newItem -> opportunities.add(0, newItem); selected = "فرصت‌ها"; selectedOpportunity = newItem }
                    }
                }
            }
        }
    }
}

@Composable
private fun Page(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 16.dp), content = content)
}

@Composable
private fun Heading(title: String, subtitle: String = "") {
    Text(title, fontSize = 21.sp, fontWeight = FontWeight.Bold, color = Ink)
    if (subtitle.isNotBlank()) Text(subtitle, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
}

@Composable
private fun Metric(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Card(Modifier.widthIn(min = 140.dp).height(105.dp), colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.fillMaxSize().padding(13.dp), verticalArrangement = Arrangement.SpaceBetween) {
            Icon(icon, null, tint = Teal, modifier = Modifier.size(21.dp))
            Text(value, fontSize = 23.sp, fontWeight = FontWeight.Bold, color = Navy)
            Text(title, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun Dashboard(onNavigate: (String) -> Unit, opportunities: List<Opportunity>) {
    Page {
        Heading("داشبورد مدیریتی", "نمای کلی از فعالیت‌های بازارگاه تأمین مالی")
        Text("سلام، به VentureBridge خوش آمدید", color = Navy, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Metric("شرکت‌های ثبت‌شده", "۲۴", Icons.Default.Business)
            Metric("فرصت‌های فعال", "۳۸", Icons.Default.Lightbulb)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            Metric("سرمایه‌گذاران", "۱۷", Icons.Default.Groups)
            Metric("در حال ارزیابی", "۹", Icons.Default.Assessment)
        }
        Spacer(Modifier.height(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("فرصت‌های منتخب", fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.weight(1f))
            Text("مشاهده همه", color = Teal, fontSize = 12.sp, modifier = Modifier.clickable { onNavigate("فرصت‌ها") })
        }
        opportunities.take(2).forEach { OpportunityCard(it) { onNavigate("فرصت‌ها") } }
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Navy), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth().clickable { onNavigate("ثبت فرصت") }) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("فرصت جدیدی برای تأمین مالی دارید؟", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("پروژه خود را ثبت کنید تا بررسی شود", color = Color(0xFFB9D2DF), fontSize = 12.sp)
                }
                Icon(Icons.Default.ArrowBack, null, tint = Color.White)
            }
        }
        Text("* تمام آمار و فرصت‌های این نسخه نمایشی و غیرواقعی هستند.", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun Opportunities(search: String, onSearch: (String) -> Unit, onSelect: (Opportunity) -> Unit, opportunities: List<Opportunity>) {
    Page {
        Heading("فرصت‌های سرمایه‌گذاری", "پروژه‌های نیازمند سرمایه را جست‌وجو و بررسی کنید")
        OutlinedTextField(value = search, onValueChange = onSearch, modifier = Modifier.fillMaxWidth(), placeholder = { Text("جست‌وجوی پروژه یا صنعت") }, leadingIcon = { Icon(Icons.Default.Search, null) }, singleLine = true, shape = RoundedCornerShape(14.dp))
        Spacer(Modifier.height(12.dp))
        val filtered = opportunities.filter { it.title.contains(search) || it.industry.contains(search) || it.company.contains(search) }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) { items(filtered) { OpportunityCard(it) { onSelect(it) } } }
    }
}

@Composable
private fun OpportunityCard(item: Opportunity, onClick: () -> Unit) {
    Card(Modifier.fillMaxWidth().clickable { onClick() }, colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
        Column(Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(item.industry, color = Teal, fontSize = 11.sp, modifier = Modifier.weight(1f))
                Surface(color = Color(0xFFE8F6F4), shape = RoundedCornerShape(8.dp)) { Text(item.stage, color = Teal, fontSize = 10.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)) }
            }
            Text(item.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Navy, modifier = Modifier.padding(top = 9.dp))
            Text(item.company, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 3.dp))
            Divider(Modifier.padding(vertical = 11.dp), color = Pale)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) { Text("سرمایه موردنیاز", fontSize = 10.sp, color = Color.Gray); Text(item.amount, color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp) }
                Text("جزئیات فرصت  ←", color = Teal, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun OpportunityDetail(item: Opportunity, onBack: () -> Unit) {
    val context = LocalContext.current
    Page {
        Text("← بازگشت", color = Teal, modifier = Modifier.clickable { onBack() }.padding(bottom = 16.dp))
        Heading(item.title, item.company)
        Card(colors = CardDefaults.cardColors(containerColor = Navy), shape = RoundedCornerShape(18.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp)) {
                Text("سرمایه موردنیاز", color = Color(0xFFB9D2DF), fontSize = 12.sp)
                Text(item.amount, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("مرحله: ${item.stage}", color = Color(0xFF8DE1D8), fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(18.dp))
        Text("درباره پروژه", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(item.description, color = Ink, lineHeight = 25.sp, modifier = Modifier.padding(top = 8.dp))
        Spacer(Modifier.height(16.dp))
        Text("اطلاعات تکمیلی", fontWeight = FontWeight.Bold)
        InfoRow("حوزه فعالیت", item.industry)
        InfoRow("شرکت متقاضی", item.company)
        InfoRow("نوع تأمین مالی", "قابل بررسی و پیشنهاد توسط کارشناسان")
        Spacer(Modifier.height(18.dp))
        Button(onClick = { Toast.makeText(context, "در نسخه نمایشی، درخواست اطلاعات ثبت نمی‌شود.", Toast.LENGTH_SHORT).show() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Teal)) { Text("درخواست اطلاعات بیشتر") }
        Text("این فرصت نمونه، فرضی است و پیشنهاد سرمایه‌گذاری محسوب نمی‌شود.", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 9.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(value, color = Ink, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.End)
    }
    Divider(color = Pale)
}

@Composable
private fun Companies() {
    Page {
        Heading("شرکت‌ها", "نمونه‌ای از شرکت‌های حاضر در بازارگاه")
        listOf("زیست‌پژوهان نوآور" to "زیست‌فناوری و دارو", "پارس‌تشخیص" to "تجهیزات پزشکی", "توان‌آوران آینده" to "انرژی").forEachIndexed { i, pair ->
            Card(Modifier.fillMaxWidth().padding(bottom = 10.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(15.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = Color(0xFFE8F6F4), shape = RoundedCornerShape(12.dp)) { Icon(Icons.Default.Business, null, tint = Teal, modifier = Modifier.padding(12.dp)) }
                    Column(Modifier.weight(1f).padding(start = 12.dp)) { Text(pair.first, fontWeight = FontWeight.Bold); Text(pair.second, color = Color.Gray, fontSize = 12.sp) }
                    Text(if (i == 0) "تأیید اولیه" else "در انتظار بررسی", color = Teal, fontSize = 10.sp)
                }
            }
        }
        Text("پروفایل هر شرکت می‌تواند شامل چند محصول و چند پروژه مستقل باشد.", color = Color.Gray, fontSize = 11.sp)
    }
}

@Composable
private fun Investors() {
    Page {
        Heading("شبکه سرمایه‌گذاران", "سرمایه‌گذاران را بر اساس نوع و حوزه فعالیت بشناسید")
        val investors = listOf(Triple("صندوق سرمایه‌گذاری خطرپذیر نوآور", "VC", "فناوری و سلامت"), Triple("بازوی سرمایه‌گذاری شرکتی پیشرو", "CVC", "صنعت و انرژی"), Triple("بانک توسعه کسب‌وکار", "بانک", "تسهیلات و سرمایه در گردش"), Triple("صندوق پژوهش و فناوری", "صندوق", "شرکت‌های فناور"))
        investors.forEach { (name, type, field) ->
            Card(Modifier.fillMaxWidth().padding(bottom = 10.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(15.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) { Text(name, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); Surface(color = Color(0xFFE8F6F4), shape = RoundedCornerShape(8.dp)) { Text(type, color = Teal, fontSize = 10.sp, modifier = Modifier.padding(7.dp)) } }
                    Text("حوزه موردعلاقه: $field", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(top = 7.dp))
                    Text("اطلاعات نمونه برای نمایش ساختار پلتفرم", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp))
                }
            }
        }
    }
}

@Composable
private fun Assessment() {
    Page {
        Heading("مرکز ارزیابی فرصت‌ها", "بررسی چندبعدی پیش از معرفی به سرمایه‌گذاران")
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("نمونه پرونده: توسعه واکسن نوترکیب HIV", fontWeight = FontWeight.Bold)
                Text("امتیاز آمادگی نمایشی", color = Color.Gray, fontSize = 11.sp, modifier = Modifier.padding(top = 12.dp))
                Row(verticalAlignment = Alignment.Bottom) { Text("۸۶", color = Teal, fontSize = 32.sp, fontWeight = FontWeight.Bold); Text(" از ۱۰۰", color = Color.Gray, modifier = Modifier.padding(bottom = 5.dp)) }
                LinearProgressIndicator(progress = { .86f }, modifier = Modifier.fillMaxWidth().height(8.dp), color = Teal, trackColor = Pale)
                Text("این امتیاز صرفاً نمایشی است و ارزیابی واقعی یا رتبه اعتباری نیست.", color = Color.Gray, fontSize = 10.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }
        Spacer(Modifier.height(14.dp))
        listOf("بررسی حقوقی" to "مدارک ثبتی و مجوزها", "بررسی فناوری" to "بلوغ فناوری و مالکیت فکری", "بررسی بازار" to "مشتریان، رقبا و اندازه بازار", "بررسی مالی" to "جریان نقدی و پیش‌بینی مالی", "بررسی سرمایه‌گذاری" to "ساختار سرمایه، ریسک و بازده").forEachIndexed { i, row ->
            Card(Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(if (i < 3) Icons.Default.CheckCircle else Icons.Default.Schedule, null, tint = if (i < 3) Teal else Color(0xFFE0A23D))
                    Column(Modifier.padding(start = 10.dp)) { Text(row.first, fontWeight = FontWeight.SemiBold); Text(row.second, fontSize = 11.sp, color = Color.Gray) }
                }
            }
        }
    }
}

@Composable
private fun RegisterOpportunity(company: String, onCompany: (String) -> Unit, project: String, onProject: (String) -> Unit, industry: String, onIndustry: (String) -> Unit, amount: String, onAmount: (String) -> Unit, submitted: Boolean, onSubmitted: (Boolean) -> Unit, onSave: (Opportunity) -> Unit) {
    Page {
        Heading("ثبت فرصت جدید", "اطلاعات اولیه پروژه نیازمند سرمایه را وارد کنید")
        OutlinedTextField(company, onCompany, label = { Text("نام شرکت") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(project, onProject, label = { Text("نام پروژه") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
        Spacer(Modifier.height(10.dp))
        Text("حوزه فعالیت", fontSize = 12.sp, color = Color.Gray)
        var expanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) { Text(industry, modifier = Modifier.weight(1f)); Icon(Icons.Default.ArrowDropDown, null) }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                listOf("زیست‌فناوری و دارو", "تجهیزات پزشکی", "انرژی", "فناوری اطلاعات", "کشاورزی و غذا", "سایر").forEach { DropdownMenuItem(text = { Text(it) }, onClick = { onIndustry(it); expanded = false }) }
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(amount, onAmount, label = { Text("سرمایه موردنیاز (تومان)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true)
        Spacer(Modifier.height(14.dp))
        var stage by remember { mutableStateOf("نمونه اولیه") }
        var financing by remember { mutableStateOf("سهامی / مشارکت") }
        var useOfFunds by remember { mutableStateOf("") }
        var market by remember { mutableStateOf("") }
        var expectedReturn by remember { mutableStateOf("") }
        Spacer(Modifier.height(10.dp))
        Text("مرحله توسعه پروژه", fontSize = 12.sp, color = Color.Gray)
        var stageExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { stageExpanded = true }, modifier = Modifier.fillMaxWidth()) { Text(stage, modifier = Modifier.weight(1f)); Icon(Icons.Default.ArrowDropDown, null) }
            DropdownMenu(expanded = stageExpanded, onDismissRequest = { stageExpanded = false }) {
                listOf("ایده", "نمونه اولیه", "آزمایش و اعتبارسنجی", "آماده تجاری‌سازی", "در حال توسعه بازار").forEach { DropdownMenuItem(text = { Text(it) }, onClick = { stage = it; stageExpanded = false }) }
            }
        }
        Spacer(Modifier.height(10.dp))
        Text("روش تأمین مالی پیشنهادی", fontSize = 12.sp, color = Color.Gray)
        var financeExpanded by remember { mutableStateOf(false) }
        Box {
            OutlinedButton(onClick = { financeExpanded = true }, modifier = Modifier.fillMaxWidth()) { Text(financing, modifier = Modifier.weight(1f)); Icon(Icons.Default.ArrowDropDown, null) }
            DropdownMenu(expanded = financeExpanded, onDismissRequest = { financeExpanded = false }) {
                listOf("سهامی / مشارکت", "تسهیلات و بدهی", "تأمین مالی جمعی", "اوراق بهادار", "لیزینگ", "پیشنهاد روش مناسب توسط VentureBridge").forEach { DropdownMenuItem(text = { Text(it) }, onClick = { financing = it; financeExpanded = false }) }
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(useOfFunds, { useOfFunds = it }, label = { Text("محل مصرف سرمایه") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(market, { market = it }, label = { Text("بازار هدف و مشتریان") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(expectedReturn, { expectedReturn = it }, label = { Text("بازده یا روش بازپرداخت مورد انتظار") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
        Text("در نسخه کامل عملیاتی، مدارک ثبتی، صورت‌های مالی، مجوزها، پیش‌بینی جریان نقدی، ریسک‌ها و اسناد محرمانه نیز به پرونده افزوده می‌شوند.", color = Color.Gray, fontSize = 11.sp, lineHeight = 20.sp, modifier = Modifier.padding(top = 10.dp))
        Spacer(Modifier.height(18.dp))
        Button(onClick = { onSubmitted(true); onSave(Opportunity(project, company, industry, "$amount تومان", stage, "روش تأمین مالی: $financing. محل مصرف سرمایه: $useOfFunds. بازار هدف: $market. بازده/بازپرداخت: $expectedReturn.")) }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Teal), enabled = company.isNotBlank() && project.isNotBlank() && amount.isNotBlank()) { Text("ثبت آزمایشی فرصت") }
        if (submitted) {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F6F4)), modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
                Text("فرصت به فهرست فرصت‌های همین اجرای آزمایشی اضافه شد. با بستن برنامه، اطلاعات پاک می‌شود.", color = Color(0xFF126C67), modifier = Modifier.padding(14.dp), fontSize = 12.sp)
            }
        }
    }
}
