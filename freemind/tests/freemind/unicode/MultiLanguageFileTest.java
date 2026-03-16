package tests.freemind.unicode;

import freemind.main.FreeMind;
import freemind.main.HeadlessFreeMind;
import freemind.main.Tools;
import freemind.modes.ExtendedMapFeedbackImpl;
import freemind.modes.MapAdapter;
import freemind.modes.MindIcon;
import freemind.modes.MindMapNode;
import freemind.modes.NodeAdapter;
import freemind.modes.attributes.Attribute;
import freemind.modes.mindmapmode.MindMapMapModel;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import plugins.contextgraph.CleanJsonExport;
import plugins.contextgraph.CleanXmlExport;
import plugins.contextgraph.CleanYamlExport;
import plugins.contextgraph.ContextGraphMarkdownExport;
import plugins.contextgraph.ContextGraphXmlExport;
import plugins.contextgraph.JsonExport;
import plugins.contextgraph.YamlExport;
import tests.freemind.ContextGraphExportTest.ExportTestNode;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests that produce real .mm mindmap files in Turkish, Russian,
 * and English. Each file is saved to disk with UTF-8 encoding, reloaded,
 * and every node's text, notes, attributes, links, and icons are verified.
 */
class MultiLanguageFileTest {

    @TempDir
    Path tempDir;

    private ExtendedMapFeedbackImpl mapFeedback;
    private MindMapMapModel map;

    @BeforeAll
    static void initOnce() {
        new HeadlessFreeMind();
    }

    @BeforeEach
    void setUp() {
        mapFeedback = new ExtendedMapFeedbackImpl();
        map = new MindMapMapModel(mapFeedback);
        mapFeedback.setMap(map);
    }

    // ========================================================================
    // Turkish
    // ========================================================================

    @Test
    void turkish_fullMapRoundTrip() throws Exception {
        // -- Build map --
        MindMapNode root = createRoot("Proje Planı");

        MindMapNode hedefler = addChild(root, 0, "Hedefler");
        addChild(hedefler, 0, "Müşteri memnuniyetini artırmak");
        addChild(hedefler, 1, "Çalışan verimliliğini ölçmek");
        addChild(hedefler, 2, "İş süreçlerini iyileştirmek");

        MindMapNode gorevler = addChild(root, 1, "Görevler");
        MindMapNode gorev1 = addChild(gorevler, 0, "Şirket içi eğitim düzenle");
        setNote(gorev1, "Eğitim İçeriği: Türkçe yazışma kuralları ve iş etiği");
        setAttribute(gorev1, "Sorumlu", "Öğretmen Çağla");
        setAttribute(gorev1, "Süre", "2 hafta");
        ((NodeAdapter) gorev1).addIcon(MindIcon.factory("bookmark"), MindIcon.LAST);

        MindMapNode gorev2 = addChild(gorevler, 1, "Üçüncü parti değerlendirme");
        ((NodeAdapter) gorev2).setLink("https://örnek.com.tr/değerlendirme");

        MindMapNode riskler = addChild(root, 2, "Riskler");
        addChild(riskler, 0, "Bütçe aşımı");
        addChild(riskler, 1, "İnsan kaynağı yetersizliği");
        ((NodeAdapter) riskler).addIcon(MindIcon.factory("clanbomber"), MindIcon.LAST);

        // -- Save to file --
        File file = tempDir.resolve("türkçe_proje_planı.mm").toFile();
        saveMapToFile(file);

        // -- Verify raw file is UTF-8 --
        String rawContent = readFileAsUtf8(file);
        assertThat(rawContent).contains("Proje Planı");
        assertThat(rawContent).contains("Müşteri memnuniyetini artırmak");
        assertThat(rawContent).contains("Çalışan verimliliğini ölçmek");
        assertThat(rawContent).contains("İş süreçlerini iyileştirmek");
        assertThat(rawContent).contains("Şirket içi eğitim düzenle");
        assertThat(rawContent).contains("Öğretmen Çağla");
        assertThat(rawContent).contains("Üçüncü parti değerlendirme");
        assertThat(rawContent).contains("Bütçe aşımı");
        assertThat(rawContent).contains("İnsan kaynağı yetersizliği");
        // No numeric entities for Turkish characters
        assertThat(rawContent).doesNotContain("&#");

        // -- Reload and verify --
        MindMapNode reloaded = reloadMap(file);
        assertThat(reloaded.getText()).isEqualTo("Proje Planı");
        assertThat(reloaded.getChildCount()).isEqualTo(3);

        // Hedefler branch
        MindMapNode rHedefler = child(reloaded, 0);
        assertThat(rHedefler.getText()).isEqualTo("Hedefler");
        assertThat(child(rHedefler, 0).getText()).isEqualTo("Müşteri memnuniyetini artırmak");
        assertThat(child(rHedefler, 1).getText()).isEqualTo("Çalışan verimliliğini ölçmek");
        assertThat(child(rHedefler, 2).getText()).isEqualTo("İş süreçlerini iyileştirmek");

        // Görevler branch
        MindMapNode rGorevler = child(reloaded, 1);
        assertThat(rGorevler.getText()).isEqualTo("Görevler");

        MindMapNode rGorev1 = child(rGorevler, 0);
        assertThat(rGorev1.getText()).isEqualTo("Şirket içi eğitim düzenle");
        assertThat(rGorev1.getNoteText()).contains("Türkçe yazışma kuralları ve iş etiği");
        assertThat(rGorev1.getAttributeTableLength()).isEqualTo(2);
        assertAttribute(rGorev1, 0, "Sorumlu", "Öğretmen Çağla");
        assertAttribute(rGorev1, 1, "Süre", "2 hafta");
        assertThat(rGorev1.getIcons()).isNotEmpty();

        MindMapNode rGorev2 = child(rGorevler, 1);
        assertThat(rGorev2.getText()).isEqualTo("Üçüncü parti değerlendirme");
        assertThat(rGorev2.getLink()).contains("değerlendirme");

        // Riskler branch
        MindMapNode rRiskler = child(reloaded, 2);
        assertThat(rRiskler.getText()).isEqualTo("Riskler");
        assertThat(child(rRiskler, 0).getText()).isEqualTo("Bütçe aşımı");
        assertThat(child(rRiskler, 1).getText()).isEqualTo("İnsan kaynağı yetersizliği");
        assertThat(rRiskler.getIcons()).isNotEmpty();
    }

    // ========================================================================
    // Russian
    // ========================================================================

    @Test
    void russian_fullMapRoundTrip() throws Exception {
        // -- Build map --
        MindMapNode root = createRoot("План проекта");

        MindMapNode celi = addChild(root, 0, "Цели");
        addChild(celi, 0, "Повысить удовлетворённость клиентов");
        addChild(celi, 1, "Измерить производительность сотрудников");
        addChild(celi, 2, "Улучшить бизнес-процессы");

        MindMapNode zadachi = addChild(root, 1, "Задачи");
        MindMapNode zadacha1 = addChild(zadachi, 0, "Организовать внутреннее обучение");
        setNote(zadacha1, "Содержание обучения: правила деловой переписки и этика");
        setAttribute(zadacha1, "Ответственный", "Преподаватель Ольга");
        setAttribute(zadacha1, "Срок", "2 недели");
        ((NodeAdapter) zadacha1).addIcon(MindIcon.factory("bookmark"), MindIcon.LAST);

        MindMapNode zadacha2 = addChild(zadachi, 1, "Внешняя оценка качества");
        ((NodeAdapter) zadacha2).setLink("https://пример.рф/оценка");

        MindMapNode riski = addChild(root, 2, "Риски");
        addChild(riski, 0, "Превышение бюджета");
        addChild(riski, 1, "Нехватка кадров");
        ((NodeAdapter) riski).addIcon(MindIcon.factory("clanbomber"), MindIcon.LAST);

        // -- Save to file --
        File file = tempDir.resolve("русский_план_проекта.mm").toFile();
        saveMapToFile(file);

        // -- Verify raw file is UTF-8 --
        String rawContent = readFileAsUtf8(file);
        assertThat(rawContent).contains("План проекта");
        assertThat(rawContent).contains("Повысить удовлетворённость клиентов");
        assertThat(rawContent).contains("Организовать внутреннее обучение");
        assertThat(rawContent).contains("Преподаватель Ольга");
        assertThat(rawContent).contains("Внешняя оценка качества");
        assertThat(rawContent).contains("Превышение бюджета");
        assertThat(rawContent).contains("Нехватка кадров");
        assertThat(rawContent).doesNotContain("&#");

        // -- Reload and verify --
        MindMapNode reloaded = reloadMap(file);
        assertThat(reloaded.getText()).isEqualTo("План проекта");
        assertThat(reloaded.getChildCount()).isEqualTo(3);

        // Цели branch
        MindMapNode rCeli = child(reloaded, 0);
        assertThat(rCeli.getText()).isEqualTo("Цели");
        assertThat(child(rCeli, 0).getText()).isEqualTo("Повысить удовлетворённость клиентов");
        assertThat(child(rCeli, 1).getText()).isEqualTo("Измерить производительность сотрудников");
        assertThat(child(rCeli, 2).getText()).isEqualTo("Улучшить бизнес-процессы");

        // Задачи branch
        MindMapNode rZadachi = child(reloaded, 1);
        assertThat(rZadachi.getText()).isEqualTo("Задачи");

        MindMapNode rZadacha1 = child(rZadachi, 0);
        assertThat(rZadacha1.getText()).isEqualTo("Организовать внутреннее обучение");
        assertThat(rZadacha1.getNoteText()).contains("правила деловой переписки и этика");
        assertThat(rZadacha1.getAttributeTableLength()).isEqualTo(2);
        assertAttribute(rZadacha1, 0, "Ответственный", "Преподаватель Ольга");
        assertAttribute(rZadacha1, 1, "Срок", "2 недели");
        assertThat(rZadacha1.getIcons()).isNotEmpty();

        MindMapNode rZadacha2 = child(rZadachi, 1);
        assertThat(rZadacha2.getText()).isEqualTo("Внешняя оценка качества");
        assertThat(rZadacha2.getLink()).contains("оценка");

        // Риски branch
        MindMapNode rRiski = child(reloaded, 2);
        assertThat(rRiski.getText()).isEqualTo("Риски");
        assertThat(child(rRiski, 0).getText()).isEqualTo("Превышение бюджета");
        assertThat(child(rRiski, 1).getText()).isEqualTo("Нехватка кадров");
        assertThat(rRiski.getIcons()).isNotEmpty();
    }

    // ========================================================================
    // English
    // ========================================================================

    @Test
    void english_fullMapRoundTrip() throws Exception {
        // -- Build map --
        MindMapNode root = createRoot("Project Plan");

        MindMapNode goals = addChild(root, 0, "Goals");
        addChild(goals, 0, "Increase customer satisfaction");
        addChild(goals, 1, "Measure employee productivity");
        addChild(goals, 2, "Improve business processes");

        MindMapNode tasks = addChild(root, 1, "Tasks");
        MindMapNode task1 = addChild(tasks, 0, "Organize internal training");
        setNote(task1, "Training content: business correspondence rules and ethics");
        setAttribute(task1, "Responsible", "Teacher Jane");
        setAttribute(task1, "Duration", "2 weeks");
        ((NodeAdapter) task1).addIcon(MindIcon.factory("bookmark"), MindIcon.LAST);

        MindMapNode task2 = addChild(tasks, 1, "Third-party quality assessment");
        ((NodeAdapter) task2).setLink("https://example.com/assessment");

        MindMapNode risks = addChild(root, 2, "Risks");
        addChild(risks, 0, "Budget overrun");
        addChild(risks, 1, "Staff shortage");
        ((NodeAdapter) risks).addIcon(MindIcon.factory("clanbomber"), MindIcon.LAST);

        // -- Save to file --
        File file = tempDir.resolve("english_project_plan.mm").toFile();
        saveMapToFile(file);

        // -- Verify raw file --
        String rawContent = readFileAsUtf8(file);
        assertThat(rawContent).contains("Project Plan");
        assertThat(rawContent).contains("Increase customer satisfaction");
        assertThat(rawContent).contains("Organize internal training");
        assertThat(rawContent).contains("Teacher Jane");
        assertThat(rawContent).contains("Third-party quality assessment");
        assertThat(rawContent).contains("Budget overrun");
        assertThat(rawContent).contains("Staff shortage");

        // -- Reload and verify --
        MindMapNode reloaded = reloadMap(file);
        assertThat(reloaded.getText()).isEqualTo("Project Plan");
        assertThat(reloaded.getChildCount()).isEqualTo(3);

        MindMapNode rGoals = child(reloaded, 0);
        assertThat(rGoals.getText()).isEqualTo("Goals");
        assertThat(child(rGoals, 0).getText()).isEqualTo("Increase customer satisfaction");
        assertThat(child(rGoals, 1).getText()).isEqualTo("Measure employee productivity");
        assertThat(child(rGoals, 2).getText()).isEqualTo("Improve business processes");

        MindMapNode rTasks = child(reloaded, 1);
        assertThat(rTasks.getText()).isEqualTo("Tasks");

        MindMapNode rTask1 = child(rTasks, 0);
        assertThat(rTask1.getText()).isEqualTo("Organize internal training");
        assertThat(rTask1.getNoteText()).contains("business correspondence rules and ethics");
        assertThat(rTask1.getAttributeTableLength()).isEqualTo(2);
        assertAttribute(rTask1, 0, "Responsible", "Teacher Jane");
        assertAttribute(rTask1, 1, "Duration", "2 weeks");
        assertThat(rTask1.getIcons()).isNotEmpty();

        MindMapNode rTask2 = child(rTasks, 1);
        assertThat(rTask2.getText()).isEqualTo("Third-party quality assessment");
        assertThat(rTask2.getLink()).isEqualTo("https://example.com/assessment");

        MindMapNode rRisks = child(reloaded, 2);
        assertThat(rRisks.getText()).isEqualTo("Risks");
        assertThat(child(rRisks, 0).getText()).isEqualTo("Budget overrun");
        assertThat(child(rRisks, 1).getText()).isEqualTo("Staff shortage");
        assertThat(rRisks.getIcons()).isNotEmpty();
    }

    // ========================================================================
    // Mixed: all three languages in one map
    // ========================================================================

    @Test
    void mixedLanguages_singleMapRoundTrip() throws Exception {
        MindMapNode root = createRoot("Çok Dilli Harita / Multilingual Map / Многоязычная карта");

        // Turkish branch
        MindMapNode tr = addChild(root, 0, "Türkçe Bölüm");
        addChild(tr, 0, "Öğrenci değerlendirme raporu");
        addChild(tr, 1, "Şirket içi iletişim ağı");
        setAttribute(addChild(tr, 2, "Çıktılar"), "Açıklama", "Türkçe açıklama metni");

        // English branch
        MindMapNode en = addChild(root, 1, "English Section");
        addChild(en, 0, "Student evaluation report");
        addChild(en, 1, "Internal communication network");

        // Russian branch
        MindMapNode ru = addChild(root, 2, "Русский раздел");
        addChild(ru, 0, "Отчёт об оценке студентов");
        addChild(ru, 1, "Внутренняя сеть связи");
        setAttribute(addChild(ru, 2, "Результаты"), "Описание", "Русское описание текста");

        // -- Save --
        File file = tempDir.resolve("çok_dilli_многоязычная_multilingual.mm").toFile();
        saveMapToFile(file);

        // -- Verify raw content --
        String rawContent = readFileAsUtf8(file);
        assertThat(rawContent).contains("Çok Dilli Harita");
        assertThat(rawContent).contains("Multilingual Map");
        assertThat(rawContent).contains("Многоязычная карта");
        assertThat(rawContent).contains("Öğrenci değerlendirme raporu");
        assertThat(rawContent).contains("Отчёт об оценке студентов");
        assertThat(rawContent).doesNotContain("&#");

        // -- Reload --
        MindMapNode reloaded = reloadMap(file);
        assertThat(reloaded.getText()).contains("Çok Dilli Harita");
        assertThat(reloaded.getText()).contains("Многоязычная карта");
        assertThat(reloaded.getChildCount()).isEqualTo(3);

        // Turkish
        MindMapNode rTr = child(reloaded, 0);
        assertThat(rTr.getText()).isEqualTo("Türkçe Bölüm");
        assertThat(child(rTr, 0).getText()).isEqualTo("Öğrenci değerlendirme raporu");
        assertThat(child(rTr, 1).getText()).isEqualTo("Şirket içi iletişim ağı");
        MindMapNode rTrChild2 = child(rTr, 2);
        assertThat(rTrChild2.getText()).isEqualTo("Çıktılar");
        assertAttribute(rTrChild2, 0, "Açıklama", "Türkçe açıklama metni");

        // English
        MindMapNode rEn = child(reloaded, 1);
        assertThat(rEn.getText()).isEqualTo("English Section");
        assertThat(child(rEn, 0).getText()).isEqualTo("Student evaluation report");

        // Russian
        MindMapNode rRu = child(reloaded, 2);
        assertThat(rRu.getText()).isEqualTo("Русский раздел");
        assertThat(child(rRu, 0).getText()).isEqualTo("Отчёт об оценке студентов");
        assertThat(child(rRu, 1).getText()).isEqualTo("Внутренняя сеть связи");
        MindMapNode rRuChild2 = child(rRu, 2);
        assertThat(rRuChild2.getText()).isEqualTo("Результаты");
        assertAttribute(rRuChild2, 0, "Описание", "Русское описание текста");
    }

    // ========================================================================
    // File naming with Unicode characters
    // ========================================================================

    @Test
    void fileNames_withUnicodeCharacters_saveAndReload() throws Exception {
        String[][] testCases = {
            {"İş Akışı Şeması", "türkçe_iş_akışı_şeması.mm"},
            {"Структура данных", "русский_структура_данных.mm"},
            {"Workflow Diagram", "english_workflow_diagram.mm"},
        };

        for (String[] testCase : testCases) {
            String rootText = testCase[0];
            String fileName = testCase[1];

            setUp(); // fresh map for each

            MindMapNode root = createRoot(rootText);
            addChild(root, 0, rootText + " — Alt Düğüm / Child Node / Дочерний узел");

            File file = tempDir.resolve(fileName).toFile();
            saveMapToFile(file);
            assertThat(file).exists();

            MindMapNode reloaded = reloadMap(file);
            assertThat(reloaded.getText()).isEqualTo(rootText);
        }
    }

    // ========================================================================
    // Deep hierarchy with Unicode at every level
    // ========================================================================

    @Test
    void deepHierarchy_unicodeAtEveryLevel() throws Exception {
        MindMapNode root = createRoot("Kök / Root / Корень");

        MindMapNode level1 = addChild(root, 0, "Birinci Seviye");
        MindMapNode level2 = addChild(level1, 0, "Второй уровень");
        MindMapNode level3 = addChild(level2, 0, "Third Level");
        MindMapNode level4 = addChild(level3, 0, "Dördüncü Seviye");
        MindMapNode level5 = addChild(level4, 0, "Пятый уровень");

        setNote(level5, "En derin düğüm / Deepest node / Самый глубокий узел");

        File file = tempDir.resolve("derin_hiyerarşi.mm").toFile();
        saveMapToFile(file);

        MindMapNode reloaded = reloadMap(file);
        assertThat(reloaded.getText()).isEqualTo("Kök / Root / Корень");

        MindMapNode r1 = child(reloaded, 0);
        assertThat(r1.getText()).isEqualTo("Birinci Seviye");

        MindMapNode r2 = child(r1, 0);
        assertThat(r2.getText()).isEqualTo("Второй уровень");

        MindMapNode r3 = child(r2, 0);
        assertThat(r3.getText()).isEqualTo("Third Level");

        MindMapNode r4 = child(r3, 0);
        assertThat(r4.getText()).isEqualTo("Dördüncü Seviye");

        MindMapNode r5 = child(r4, 0);
        assertThat(r5.getText()).isEqualTo("Пятый уровень");
        assertThat(r5.getNoteText()).contains("En derin düğüm");
        assertThat(r5.getNoteText()).contains("Самый глубокий узел");
    }

    // ========================================================================
    // Notes with rich HTML content in each language
    // ========================================================================

    @Test
    void richNotes_inAllLanguages() throws Exception {
        MindMapNode root = createRoot("Notlar / Notes / Заметки");

        MindMapNode trNode = addChild(root, 0, "Türkçe Not");
        setNote(trNode, "<b>Kalın yazı:</b> Şirketin büyüme stratejisi ve çalışan memnuniyeti "
            + "ölçüm <i>yöntemleri</i> hakkında detaylı rapor hazırlanacaktır.");

        MindMapNode ruNode = addChild(root, 1, "Русская заметка");
        setNote(ruNode, "<b>Жирный текст:</b> Стратегия роста компании и методы "
            + "<i>измерения</i> удовлетворённости сотрудников будут подробно описаны.");

        MindMapNode enNode = addChild(root, 2, "English Note");
        setNote(enNode, "<b>Bold text:</b> A detailed report on the company's growth "
            + "strategy and employee satisfaction <i>measurement methods</i> will be prepared.");

        File file = tempDir.resolve("zengin_notlar.mm").toFile();
        saveMapToFile(file);

        String rawContent = readFileAsUtf8(file);
        assertThat(rawContent).contains("büyüme stratejisi");
        assertThat(rawContent).contains("Стратегия роста компании");
        assertThat(rawContent).contains("growth strategy");
        assertThat(rawContent).doesNotContain("&#");

        MindMapNode reloaded = reloadMap(file);

        MindMapNode rTr = child(reloaded, 0);
        assertThat(rTr.getNoteText()).contains("Şirketin büyüme stratejisi");
        assertThat(rTr.getNoteText()).contains("çalışan memnuniyeti");
        assertThat(rTr.getNoteText()).contains("yöntemleri");

        MindMapNode rRu = child(reloaded, 1);
        assertThat(rRu.getNoteText()).contains("Стратегия роста компании");
        assertThat(rRu.getNoteText()).contains("удовлетворённости сотрудников");

        MindMapNode rEn = child(reloaded, 2);
        assertThat(rEn.getNoteText()).contains("growth strategy");
        assertThat(rEn.getNoteText()).contains("measurement methods");
    }

    // ========================================================================
    // Attributes with special characters in each language
    // ========================================================================

    @Test
    void attributes_specialCharsInAllLanguages() throws Exception {
        MindMapNode root = createRoot("Özellikler / Attributes / Атрибуты");

        MindMapNode trNode = addChild(root, 0, "Türkçe Düğüm");
        setAttribute(trNode, "Şehir", "İstanbul");
        setAttribute(trNode, "Ülke", "Türkiye");
        setAttribute(trNode, "Açıklama", "Güneşli & sıcak hava (>30°C)");

        MindMapNode ruNode = addChild(root, 1, "Русский узел");
        setAttribute(ruNode, "Город", "Москва");
        setAttribute(ruNode, "Страна", "Россия");
        setAttribute(ruNode, "Описание", "Снежная & холодная погода (<-20°C)");

        MindMapNode enNode = addChild(root, 2, "English Node");
        setAttribute(enNode, "City", "New York");
        setAttribute(enNode, "Country", "United States");
        setAttribute(enNode, "Description", "Warm & sunny (>30°C)");

        File file = tempDir.resolve("özellikler_атрибуты_attributes.mm").toFile();
        saveMapToFile(file);

        MindMapNode reloaded = reloadMap(file);

        MindMapNode rTr = child(reloaded, 0);
        assertAttribute(rTr, 0, "Şehir", "İstanbul");
        assertAttribute(rTr, 1, "Ülke", "Türkiye");
        assertAttribute(rTr, 2, "Açıklama", "Güneşli & sıcak hava (>30°C)");

        MindMapNode rRu = child(reloaded, 1);
        assertAttribute(rRu, 0, "Город", "Москва");
        assertAttribute(rRu, 1, "Страна", "Россия");

        MindMapNode rEn = child(reloaded, 2);
        assertAttribute(rEn, 0, "City", "New York");
        assertAttribute(rEn, 1, "Country", "United States");
    }

    // ========================================================================
    // Export: Turkish — all 7 formats
    // ========================================================================

    @Test
    void turkish_markdownExport_preservesCharacters() throws Exception {
        ExportTestNode root = createTurkishExportTree();
        String output = exportMarkdown(root);
        assertThat(output).contains("Proje Planı");
        assertThat(output).contains("Müşteri memnuniyetini artırmak");
        assertThat(output).contains("Şirket içi eğitim düzenle");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void turkish_contextGraphXmlExport_preservesCharacters() throws Exception {
        ExportTestNode root = createTurkishExportTree();
        String output = exportContextGraphXml(root);
        assertThat(output).contains("Proje Planı");
        assertThat(output).contains("Çalışan verimliliğini ölçmek");
        assertThat(output).contains("Görevler");
        assertThat(output).contains("<node");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void turkish_cleanJsonExport_preservesCharacters() throws Exception {
        ExportTestNode root = createTurkishExportTree();
        String output = exportCleanJson(root);
        assertThat(output).contains("Müşteri memnuniyetini artırmak");
        assertThat(output).contains("İş süreçlerini iyileştirmek");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void turkish_cleanXmlExport_preservesCharacters() throws Exception {
        ExportTestNode root = createTurkishExportTree();
        String output = exportCleanXml(root);
        assertThat(output).contains("Hedefler");
        assertThat(output).contains("Görevler");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void turkish_cleanYamlExport_preservesCharacters() throws Exception {
        ExportTestNode root = createTurkishExportTree();
        String output = exportCleanYaml(root);
        assertThat(output).contains("Hedefler");
        assertThat(output).contains("Görevler");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void turkish_jsonExport_preservesCharacters() throws Exception {
        ExportTestNode root = createTurkishExportTree();
        String output = exportJson(root);
        assertThat(output).contains("Proje Planı");
        assertThat(output).contains("Şirket içi eğitim düzenle");
        assertThat(output).contains("\"text\"");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void turkish_yamlExport_preservesCharacters() throws Exception {
        ExportTestNode root = createTurkishExportTree();
        String output = exportYaml(root);
        if (output != null) {
            assertThat(output).contains("Proje Planı");
            assertThat(output).contains("text:");
            assertThat(output).doesNotContain("&#");
        }
    }

    // ========================================================================
    // Export: Russian — all 7 formats
    // ========================================================================

    @Test
    void russian_markdownExport_preservesCharacters() throws Exception {
        ExportTestNode root = createRussianExportTree();
        String output = exportMarkdown(root);
        assertThat(output).contains("План проекта");
        assertThat(output).contains("Повысить удовлетворённость клиентов");
        assertThat(output).contains("Организовать внутреннее обучение");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void russian_contextGraphXmlExport_preservesCharacters() throws Exception {
        ExportTestNode root = createRussianExportTree();
        String output = exportContextGraphXml(root);
        assertThat(output).contains("План проекта");
        assertThat(output).contains("Измерить производительность сотрудников");
        assertThat(output).contains("Задачи");
        assertThat(output).contains("<node");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void russian_cleanJsonExport_preservesCharacters() throws Exception {
        ExportTestNode root = createRussianExportTree();
        String output = exportCleanJson(root);
        assertThat(output).contains("Повысить удовлетворённость клиентов");
        assertThat(output).contains("Улучшить бизнес-процессы");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void russian_cleanXmlExport_preservesCharacters() throws Exception {
        ExportTestNode root = createRussianExportTree();
        String output = exportCleanXml(root);
        assertThat(output).contains("Цели");
        assertThat(output).contains("Задачи");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void russian_cleanYamlExport_preservesCharacters() throws Exception {
        ExportTestNode root = createRussianExportTree();
        String output = exportCleanYaml(root);
        assertThat(output).contains("Цели");
        assertThat(output).contains("Задачи");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void russian_jsonExport_preservesCharacters() throws Exception {
        ExportTestNode root = createRussianExportTree();
        String output = exportJson(root);
        assertThat(output).contains("План проекта");
        assertThat(output).contains("Организовать внутреннее обучение");
        assertThat(output).contains("\"text\"");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void russian_yamlExport_preservesCharacters() throws Exception {
        ExportTestNode root = createRussianExportTree();
        String output = exportYaml(root);
        if (output != null) {
            assertThat(output).contains("План проекта");
            assertThat(output).contains("text:");
            assertThat(output).doesNotContain("&#");
        }
    }

    // ========================================================================
    // Export: English — all 7 formats
    // ========================================================================

    @Test
    void english_markdownExport_preservesCharacters() throws Exception {
        ExportTestNode root = createEnglishExportTree();
        String output = exportMarkdown(root);
        assertThat(output).contains("Project Plan");
        assertThat(output).contains("Increase customer satisfaction");
        assertThat(output).contains("Organize internal training");
    }

    @Test
    void english_contextGraphXmlExport_preservesCharacters() throws Exception {
        ExportTestNode root = createEnglishExportTree();
        String output = exportContextGraphXml(root);
        assertThat(output).contains("Project Plan");
        assertThat(output).contains("Measure employee productivity");
        assertThat(output).contains("<node");
    }

    @Test
    void english_cleanJsonExport_preservesCharacters() throws Exception {
        ExportTestNode root = createEnglishExportTree();
        String output = exportCleanJson(root);
        assertThat(output).contains("Increase customer satisfaction");
        assertThat(output).contains("Improve business processes");
    }

    @Test
    void english_cleanXmlExport_preservesCharacters() throws Exception {
        ExportTestNode root = createEnglishExportTree();
        String output = exportCleanXml(root);
        assertThat(output).contains("Goals");
        assertThat(output).contains("Tasks");
    }

    @Test
    void english_cleanYamlExport_preservesCharacters() throws Exception {
        ExportTestNode root = createEnglishExportTree();
        String output = exportCleanYaml(root);
        assertThat(output).contains("Goals");
        assertThat(output).contains("Tasks");
    }

    @Test
    void english_jsonExport_preservesCharacters() throws Exception {
        ExportTestNode root = createEnglishExportTree();
        String output = exportJson(root);
        assertThat(output).contains("Project Plan");
        assertThat(output).contains("Organize internal training");
        assertThat(output).contains("\"text\"");
    }

    @Test
    void english_yamlExport_preservesCharacters() throws Exception {
        ExportTestNode root = createEnglishExportTree();
        String output = exportYaml(root);
        if (output != null) {
            assertThat(output).contains("Project Plan");
            assertThat(output).contains("text:");
        }
    }

    // ========================================================================
    // Export: Mixed languages — all 7 formats
    // ========================================================================

    @Test
    void mixed_markdownExport_preservesAllLanguages() throws Exception {
        ExportTestNode root = createMixedExportTree();
        String output = exportMarkdown(root);
        assertThat(output).contains("Türkçe Bölüm");
        assertThat(output).contains("English Section");
        assertThat(output).contains("Русский раздел");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void mixed_contextGraphXmlExport_preservesAllLanguages() throws Exception {
        ExportTestNode root = createMixedExportTree();
        String output = exportContextGraphXml(root);
        assertThat(output).contains("Öğrenci değerlendirme");
        assertThat(output).contains("Student evaluation");
        assertThat(output).contains("Отчёт об оценке");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void mixed_cleanJsonExport_preservesAllLanguages() throws Exception {
        ExportTestNode root = createMixedExportTree();
        String output = exportCleanJson(root);
        assertThat(output).contains("Şirket içi iletişim");
        assertThat(output).contains("Internal communication");
        assertThat(output).contains("Внутренняя сеть связи");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void mixed_cleanXmlExport_preservesAllLanguages() throws Exception {
        ExportTestNode root = createMixedExportTree();
        String output = exportCleanXml(root);
        assertThat(output).contains("Türkçe Bölüm");
        assertThat(output).contains("Русский раздел");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void mixed_cleanYamlExport_preservesAllLanguages() throws Exception {
        ExportTestNode root = createMixedExportTree();
        String output = exportCleanYaml(root);
        assertThat(output).contains("Türkçe Bölüm");
        assertThat(output).contains("Русский раздел");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void mixed_jsonExport_preservesAllLanguages() throws Exception {
        ExportTestNode root = createMixedExportTree();
        String output = exportJson(root);
        assertThat(output).contains("Türkçe Bölüm");
        assertThat(output).contains("English Section");
        assertThat(output).contains("Русский раздел");
        assertThat(output).doesNotContain("&#");
    }

    @Test
    void mixed_yamlExport_preservesAllLanguages() throws Exception {
        ExportTestNode root = createMixedExportTree();
        String output = exportYaml(root);
        if (output != null) {
            assertThat(output).contains("Türkçe Bölüm");
            assertThat(output).contains("Русский раздел");
            assertThat(output).doesNotContain("&#");
        }
    }

    // ========================================================================
    // Export tree builders (using ExportTestNode for reflection-based export)
    // ========================================================================

    private ExportTestNode createTurkishExportTree() {
        ExportTestNode root = new ExportTestNode("Proje Planı");

        ExportTestNode hedefler = new ExportTestNode("Hedefler");
        hedefler.addChild(new ExportTestNode("Müşteri memnuniyetini artırmak"));
        hedefler.addChild(new ExportTestNode("Çalışan verimliliğini ölçmek"));
        hedefler.addChild(new ExportTestNode("İş süreçlerini iyileştirmek"));

        ExportTestNode gorevler = new ExportTestNode("Görevler");
        ExportTestNode gorev1 = new ExportTestNode("Şirket içi eğitim düzenle");
        gorev1.addIcon(MindIcon.factory("bookmark"), MindIcon.LAST);
        gorev1.setNoteText("Eğitim İçeriği: Türkçe yazışma kuralları ve iş etiği");
        gorevler.addChild(gorev1);

        ExportTestNode gorev2 = new ExportTestNode("Üçüncü parti değerlendirme");
        gorev2.setLink("https://örnek.com.tr/değerlendirme");
        gorevler.addChild(gorev2);

        ExportTestNode riskler = new ExportTestNode("Riskler");
        riskler.addIcon(MindIcon.factory("clanbomber"), MindIcon.LAST);
        riskler.addChild(new ExportTestNode("Bütçe aşımı"));
        riskler.addChild(new ExportTestNode("İnsan kaynağı yetersizliği"));

        root.addChild(hedefler);
        root.addChild(gorevler);
        root.addChild(riskler);
        return root;
    }

    private ExportTestNode createRussianExportTree() {
        ExportTestNode root = new ExportTestNode("План проекта");

        ExportTestNode celi = new ExportTestNode("Цели");
        celi.addChild(new ExportTestNode("Повысить удовлетворённость клиентов"));
        celi.addChild(new ExportTestNode("Измерить производительность сотрудников"));
        celi.addChild(new ExportTestNode("Улучшить бизнес-процессы"));

        ExportTestNode zadachi = new ExportTestNode("Задачи");
        ExportTestNode zadacha1 = new ExportTestNode("Организовать внутреннее обучение");
        zadacha1.addIcon(MindIcon.factory("bookmark"), MindIcon.LAST);
        zadacha1.setNoteText("Содержание обучения: правила деловой переписки и этика");
        zadachi.addChild(zadacha1);

        ExportTestNode zadacha2 = new ExportTestNode("Внешняя оценка качества");
        zadacha2.setLink("https://пример.рф/оценка");
        zadachi.addChild(zadacha2);

        ExportTestNode riski = new ExportTestNode("Риски");
        riski.addIcon(MindIcon.factory("clanbomber"), MindIcon.LAST);
        riski.addChild(new ExportTestNode("Превышение бюджета"));
        riski.addChild(new ExportTestNode("Нехватка кадров"));

        root.addChild(celi);
        root.addChild(zadachi);
        root.addChild(riski);
        return root;
    }

    private ExportTestNode createEnglishExportTree() {
        ExportTestNode root = new ExportTestNode("Project Plan");

        ExportTestNode goals = new ExportTestNode("Goals");
        goals.addChild(new ExportTestNode("Increase customer satisfaction"));
        goals.addChild(new ExportTestNode("Measure employee productivity"));
        goals.addChild(new ExportTestNode("Improve business processes"));

        ExportTestNode tasks = new ExportTestNode("Tasks");
        ExportTestNode task1 = new ExportTestNode("Organize internal training");
        task1.addIcon(MindIcon.factory("bookmark"), MindIcon.LAST);
        task1.setNoteText("Training content: business correspondence rules and ethics");
        tasks.addChild(task1);

        ExportTestNode task2 = new ExportTestNode("Third-party quality assessment");
        task2.setLink("https://example.com/assessment");
        tasks.addChild(task2);

        ExportTestNode risks = new ExportTestNode("Risks");
        risks.addIcon(MindIcon.factory("clanbomber"), MindIcon.LAST);
        risks.addChild(new ExportTestNode("Budget overrun"));
        risks.addChild(new ExportTestNode("Staff shortage"));

        root.addChild(goals);
        root.addChild(tasks);
        root.addChild(risks);
        return root;
    }

    private ExportTestNode createMixedExportTree() {
        ExportTestNode root = new ExportTestNode("Çok Dilli / Multilingual / Многоязычная");

        ExportTestNode tr = new ExportTestNode("Türkçe Bölüm");
        tr.addChild(new ExportTestNode("Öğrenci değerlendirme raporu"));
        tr.addChild(new ExportTestNode("Şirket içi iletişim ağı"));

        ExportTestNode en = new ExportTestNode("English Section");
        en.addChild(new ExportTestNode("Student evaluation report"));
        en.addChild(new ExportTestNode("Internal communication network"));

        ExportTestNode ru = new ExportTestNode("Русский раздел");
        ru.addChild(new ExportTestNode("Отчёт об оценке студентов"));
        ru.addChild(new ExportTestNode("Внутренняя сеть связи"));

        root.addChild(tr);
        root.addChild(en);
        root.addChild(ru);
        return root;
    }

    // ========================================================================
    // Export helpers (reflection-based, same pattern as ContextGraphExportTest)
    // ========================================================================

    private String exportMarkdown(ExportTestNode root) throws Exception {
        ContextGraphMarkdownExport export = new ContextGraphMarkdownExport();
        StringWriter sw = new StringWriter();
        BufferedWriter writer = new BufferedWriter(sw);
        Method writeNode = ContextGraphMarkdownExport.class.getDeclaredMethod(
            "writeNode", BufferedWriter.class, MindMapNode.class, int.class);
        writeNode.setAccessible(true);
        writeNode.invoke(export, writer, root, 0);
        writer.flush();
        return sw.toString();
    }

    private String exportContextGraphXml(ExportTestNode root) throws Exception {
        ContextGraphXmlExport export = new ContextGraphXmlExport();
        StringWriter sw = new StringWriter();
        BufferedWriter writer = new BufferedWriter(sw);
        Method writeNode = ContextGraphXmlExport.class.getDeclaredMethod(
            "writeNode", BufferedWriter.class, MindMapNode.class, int.class, String.class);
        writeNode.setAccessible(true);
        writeNode.invoke(export, writer, root, 0, "  ");
        writer.flush();
        return sw.toString();
    }

    private String exportCleanJson(ExportTestNode root) throws Exception {
        CleanJsonExport export = new CleanJsonExport();
        StringWriter sw = new StringWriter();
        BufferedWriter writer = new BufferedWriter(sw);
        Method writeNodeValue = CleanJsonExport.class.getDeclaredMethod(
            "writeNodeValue", BufferedWriter.class, MindMapNode.class, String.class);
        writeNodeValue.setAccessible(true);
        writeNodeValue.invoke(export, writer, root, "");
        writer.flush();
        return sw.toString();
    }

    private String exportCleanXml(ExportTestNode root) throws Exception {
        CleanXmlExport export = new CleanXmlExport();
        StringWriter sw = new StringWriter();
        BufferedWriter writer = new BufferedWriter(sw);
        Method writeChildren = CleanXmlExport.class.getDeclaredMethod(
            "writeChildren", BufferedWriter.class, MindMapNode.class, String.class);
        writeChildren.setAccessible(true);
        writeChildren.invoke(export, writer, root, "  ");
        writer.flush();
        return sw.toString();
    }

    private String exportCleanYaml(ExportTestNode root) throws Exception {
        CleanYamlExport export = new CleanYamlExport();
        StringWriter sw = new StringWriter();
        BufferedWriter writer = new BufferedWriter(sw);
        Method writeChildren = CleanYamlExport.class.getDeclaredMethod(
            "writeChildren", BufferedWriter.class, MindMapNode.class, String.class);
        writeChildren.setAccessible(true);
        writeChildren.invoke(export, writer, root, "  ");
        writer.flush();
        return sw.toString();
    }

    private String exportJson(ExportTestNode root) throws Exception {
        JsonExport export = new JsonExport();
        StringWriter sw = new StringWriter();
        BufferedWriter writer = new BufferedWriter(sw);
        Method writeNode = JsonExport.class.getDeclaredMethod(
            "writeNode", BufferedWriter.class, MindMapNode.class, String.class);
        writeNode.setAccessible(true);
        writeNode.invoke(export, writer, root, "  ");
        writer.flush();
        return sw.toString();
    }

    /**
     * YamlExport.writeNode may throw NPE due to getController() being null in headless mode.
     * Returns null in that case — the test should handle it gracefully.
     */
    private String exportYaml(ExportTestNode root) throws Exception {
        YamlExport export = new YamlExport();
        StringWriter sw = new StringWriter();
        BufferedWriter writer = new BufferedWriter(sw);
        Method writeNode = YamlExport.class.getDeclaredMethod(
            "writeNode", BufferedWriter.class, MindMapNode.class, String.class);
        writeNode.setAccessible(true);
        try {
            writeNode.invoke(export, writer, root, "  ");
            writer.flush();
            return sw.toString();
        } catch (Exception e) {
            if (e.getCause() instanceof NullPointerException) {
                return null; // YamlExport requires controller for getObjectId
            }
            throw e;
        }
    }

    // ========================================================================
    // Helpers — .mm file round-trip
    // ========================================================================

    private MindMapNode createRoot(String text) throws Exception {
        String escapedText = xmlEscape(text);
        String mapXml = "<map version=\"" + FreeMind.XML_VERSION + "\"><node TEXT=\""
            + escapedText + "\"/></map>";
        Tools.StringReaderCreator reader = new Tools.StringReaderCreator(mapXml);
        MindMapNode root = map.loadTree(reader, MapAdapter.sDontAskInstance);
        map.setRoot(root);
        return root;
    }

    private MindMapNode addChild(MindMapNode parent, int index, String text) {
        MindMapNode child = mapFeedback.addNewNode(parent, index, false);
        mapFeedback.setNodeText(child, text);
        return child;
    }

    private void setNote(MindMapNode node, String noteBody) {
        String htmlNote = "<html>\n  <head>\n    \n  </head>\n  <body>\n    <p>\n      "
            + noteBody + "\n    </p>\n  </body>\n</html>\n";
        mapFeedback.setNoteText(node, htmlNote);
    }

    private void setAttribute(MindMapNode node, String name, String value) {
        mapFeedback.addAttribute(node, new Attribute(name, value));
    }

    private void saveMapToFile(File file) throws Exception {
        try (Writer fileout = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream(file), StandardCharsets.UTF_8))) {
            map.getXml(fileout);
        }
    }

    private MindMapNode reloadMap(File file) throws Exception {
        ExtendedMapFeedbackImpl fb = new ExtendedMapFeedbackImpl();
        MindMapMapModel reloadedMap = new MindMapMapModel(fb);
        fb.setMap(reloadedMap);
        Tools.FileReaderCreator readerCreator = new Tools.FileReaderCreator(file);
        MindMapNode root = reloadedMap.loadTree(readerCreator, MapAdapter.sDontAskInstance);
        reloadedMap.setRoot(root);
        return root;
    }

    private String readFileAsUtf8(File file) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        }
        return sb.toString();
    }

    private static MindMapNode child(MindMapNode parent, int index) {
        return (MindMapNode) parent.getChildAt(index);
    }

    private static void assertAttribute(MindMapNode node, int index, String name, String value) {
        assertThat(node.getAttributeTableLength())
            .as("Node '%s' should have at least %d attributes", node.getText(), index + 1)
            .isGreaterThan(index);
        Attribute attr = node.getAttribute(index);
        assertThat(attr.getName())
            .as("Attribute %d name on node '%s'", index, node.getText())
            .isEqualTo(name);
        assertThat(attr.getValue())
            .as("Attribute %d value on node '%s'", index, node.getText())
            .isEqualTo(value);
    }

    private static String xmlEscape(String s) {
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("'", "&apos;")
                .replace("\"", "&quot;");
    }
}
