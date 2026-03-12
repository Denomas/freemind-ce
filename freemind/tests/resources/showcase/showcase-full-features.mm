<map version="1.0.1">
<node TEXT="FreeMind CE&#xa;Feature Showcase" COLOR="#000000" BACKGROUND_COLOR="#e8f4fd" STYLE="bubble">
<font NAME="SansSerif" SIZE="20" BOLD="true"/>
<edge COLOR="#2196F3" WIDTH="4"/>
<cloud COLOR="#e3f2fd"/>
<icon BUILTIN="idea"/>

<!-- Branch 1: Project Management -->
<node TEXT="Project Management" POSITION="right" COLOR="#1565C0" FOLDED="false">
<font NAME="SansSerif" SIZE="16" BOLD="true"/>
<edge COLOR="#1976D2" STYLE="bezier" WIDTH="2"/>
<cloud COLOR="#bbdefb"/>
<icon BUILTIN="launch"/>
<node TEXT="Sprint Planning" COLOR="#1976D2">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="calendar"/>
<node TEXT="Backlog Grooming" COLOR="#424242">
<icon BUILTIN="list"/>
<node TEXT="User Stories" COLOR="#616161">
<icon BUILTIN="edit"/>
</node>
<node TEXT="Story Points" COLOR="#616161">
<icon BUILTIN="full-1"/>
</node>
</node>
<node TEXT="Sprint Goals" COLOR="#424242">
<icon BUILTIN="flag-green"/>
<richcontent TYPE="NOTE"><html><head></head><body><p>Define clear, achievable goals for each sprint iteration. Review with stakeholders before committing.</p></body></html></richcontent>
</node>
<node TEXT="Velocity Tracking" COLOR="#424242">
<icon BUILTIN="wizard"/>
</node>
</node>
<node TEXT="Task Board" COLOR="#1976D2">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="xmag"/>
<node TEXT="To Do" COLOR="#f44336">
<icon BUILTIN="stop-sign"/>
<node TEXT="Design API endpoints"/>
<node TEXT="Write unit tests"/>
<node TEXT="Security audit"/>
</node>
<node TEXT="In Progress" COLOR="#ff9800">
<icon BUILTIN="hourglass"/>
<node TEXT="Frontend refactor"/>
<node TEXT="Database migration"/>
</node>
<node TEXT="Done" COLOR="#4caf50">
<icon BUILTIN="button_ok"/>
<node TEXT="CI/CD pipeline"/>
<node TEXT="Code review"/>
<node TEXT="Documentation"/>
</node>
</node>
<node TEXT="Team Roles" COLOR="#1976D2">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="group"/>
<node TEXT="Product Owner" COLOR="#424242">
<icon BUILTIN="male1"/>
</node>
<node TEXT="Scrum Master" COLOR="#424242">
<icon BUILTIN="male2"/>
</node>
<node TEXT="Developers" COLOR="#424242">
<icon BUILTIN="family"/>
<node TEXT="Backend (3)"/>
<node TEXT="Frontend (2)"/>
<node TEXT="DevOps (1)"/>
</node>
</node>
</node>

<!-- Branch 2: Software Architecture -->
<node TEXT="Software Architecture" POSITION="right" COLOR="#2E7D32" FOLDED="false">
<font NAME="SansSerif" SIZE="16" BOLD="true"/>
<edge COLOR="#388E3C" STYLE="sharp_bezier" WIDTH="2"/>
<cloud COLOR="#c8e6c9"/>
<icon BUILTIN="penguin"/>
<node TEXT="Frontend" COLOR="#388E3C">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="desktop_new"/>
<node TEXT="React Components" COLOR="#424242">
<node TEXT="Header"/>
<node TEXT="Sidebar"/>
<node TEXT="Dashboard"/>
<node TEXT="Settings"/>
</node>
<node TEXT="State Management" COLOR="#424242">
<icon BUILTIN="revision"/>
<node TEXT="Redux Store"/>
<node TEXT="Actions &amp; Reducers"/>
</node>
</node>
<node TEXT="Backend" COLOR="#388E3C">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="freemind_butterfly"/>
<node TEXT="REST API" COLOR="#424242">
<icon BUILTIN="internet"/>
<node TEXT="GET /api/users"/>
<node TEXT="POST /api/projects"/>
<node TEXT="PUT /api/tasks/:id"/>
<node TEXT="DELETE /api/tasks/:id"/>
</node>
<node TEXT="Database" COLOR="#424242">
<icon BUILTIN="attach"/>
<node TEXT="PostgreSQL 16"/>
<node TEXT="Redis Cache"/>
<node TEXT="Migrations"/>
</node>
<node TEXT="Authentication" COLOR="#424242">
<icon BUILTIN="password"/>
<node TEXT="JWT Tokens"/>
<node TEXT="OAuth 2.0"/>
<node TEXT="2FA Support"/>
</node>
</node>
<node TEXT="Infrastructure" COLOR="#388E3C">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="clanbomber"/>
<node TEXT="Docker Compose" COLOR="#424242"/>
<node TEXT="Kubernetes" COLOR="#424242"/>
<node TEXT="Terraform" COLOR="#424242"/>
<node TEXT="GitHub Actions CI" COLOR="#424242"/>
</node>
</node>

<!-- Branch 3: Learning Path -->
<node TEXT="Learning &amp; Knowledge" POSITION="left" COLOR="#6A1B9A" FOLDED="false">
<font NAME="SansSerif" SIZE="16" BOLD="true"/>
<edge COLOR="#7B1FA2" STYLE="linear" WIDTH="2"/>
<cloud COLOR="#e1bee7"/>
<icon BUILTIN="bookmark"/>
<node TEXT="Programming Languages" COLOR="#7B1FA2">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="pencil"/>
<node TEXT="Java 21" COLOR="#424242">
<icon BUILTIN="bee"/>
<node TEXT="Records &amp; Sealed Classes"/>
<node TEXT="Virtual Threads"/>
<node TEXT="Pattern Matching"/>
</node>
<node TEXT="Python 3.12" COLOR="#424242">
<icon BUILTIN="penguin"/>
<node TEXT="Type Hints"/>
<node TEXT="AsyncIO"/>
<node TEXT="Dataclasses"/>
</node>
<node TEXT="TypeScript 5" COLOR="#424242">
<icon BUILTIN="desktop_new"/>
<node TEXT="Generics"/>
<node TEXT="Decorators"/>
<node TEXT="Utility Types"/>
</node>
</node>
<node TEXT="Books &amp; Resources" COLOR="#7B1FA2">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="info"/>
<node TEXT="Clean Code" COLOR="#424242" LINK="https://www.goodreads.com/book/show/3735293-clean-code">
<icon BUILTIN="book"/>
</node>
<node TEXT="Design Patterns" COLOR="#424242">
<icon BUILTIN="book"/>
</node>
<node TEXT="Refactoring" COLOR="#424242">
<icon BUILTIN="book"/>
</node>
</node>
</node>

<!-- Branch 4: Personal Goals -->
<node TEXT="Goals &amp; Milestones" POSITION="left" COLOR="#BF360C" FOLDED="false">
<font NAME="SansSerif" SIZE="16" BOLD="true"/>
<edge COLOR="#D84315" STYLE="bezier" WIDTH="2"/>
<cloud COLOR="#ffccbc"/>
<icon BUILTIN="flag"/>
<node TEXT="Q1 2026" COLOR="#D84315">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="calendar"/>
<node TEXT="Launch v2.0" COLOR="#4caf50">
<icon BUILTIN="button_ok"/>
<richcontent TYPE="NOTE"><html><head></head><body><p><b>Status:</b> Complete<br/><b>Date:</b> January 15, 2026<br/><b>Details:</b> Major release with all planned features shipped successfully.</p></body></html></richcontent>
</node>
<node TEXT="Hire 2 engineers" COLOR="#4caf50">
<icon BUILTIN="button_ok"/>
</node>
<node TEXT="ISO 27001 Cert" COLOR="#ff9800">
<icon BUILTIN="hourglass"/>
</node>
</node>
<node TEXT="Q2 2026" COLOR="#D84315">
<font NAME="SansSerif" SIZE="14"/>
<icon BUILTIN="calendar"/>
<node TEXT="Mobile App Beta" COLOR="#f44336">
<icon BUILTIN="stop-sign"/>
</node>
<node TEXT="1000 DAU target" COLOR="#f44336">
<icon BUILTIN="stop-sign"/>
</node>
<node TEXT="Conference Talk" COLOR="#ff9800">
<icon BUILTIN="hourglass"/>
</node>
</node>
</node>

<!-- Branch 5: Meeting Notes -->
<node TEXT="Meeting Notes" POSITION="right" COLOR="#F57F17" FOLDED="false">
<font NAME="SansSerif" SIZE="16" BOLD="true"/>
<edge COLOR="#F9A825" STYLE="sharp_bezier" WIDTH="2"/>
<icon BUILTIN="knotify"/>
<node TEXT="2026-03-10 Standup" COLOR="#F9A825">
<font NAME="SansSerif" SIZE="14" ITALIC="true"/>
<icon BUILTIN="clock"/>
<node TEXT="Alice: Completed auth module" COLOR="#424242"/>
<node TEXT="Bob: Blocked on API design" COLOR="#f44336">
<icon BUILTIN="stop"/>
</node>
<node TEXT="Carol: Starting QA tests" COLOR="#424242"/>
</node>
<node TEXT="2026-03-07 Retro" COLOR="#F9A825">
<font NAME="SansSerif" SIZE="14" ITALIC="true"/>
<icon BUILTIN="clock"/>
<node TEXT="What went well" COLOR="#4caf50">
<icon BUILTIN="ksmiletris"/>
<node TEXT="Deployment automation"/>
<node TEXT="Code review speed"/>
</node>
<node TEXT="What to improve" COLOR="#f44336">
<icon BUILTIN="clanbomber"/>
<node TEXT="Test coverage"/>
<node TEXT="Documentation gaps"/>
</node>
</node>
</node>

<!-- Branch 6: Quick Links -->
<node TEXT="Quick Links" POSITION="left" COLOR="#004D40" FOLDED="false">
<font NAME="SansSerif" SIZE="16" BOLD="true"/>
<edge COLOR="#00695C" STYLE="bezier" WIDTH="2"/>
<icon BUILTIN="internet"/>
<node TEXT="GitHub Repository" COLOR="#00695C" LINK="https://github.com/example/project">
<icon BUILTIN="internet"/>
</node>
<node TEXT="Documentation Wiki" COLOR="#00695C" LINK="https://wiki.example.com">
<icon BUILTIN="help"/>
</node>
<node TEXT="Jira Board" COLOR="#00695C" LINK="https://jira.example.com">
<icon BUILTIN="launch"/>
</node>
<node TEXT="Slack Channel" COLOR="#00695C" LINK="https://slack.example.com">
<icon BUILTIN="knotify"/>
</node>
</node>
</node>
</map>
