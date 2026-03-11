package tests.freemind.property.generators;

import freemind.controller.actions.generated.instance.*;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Combinators;
import net.jqwik.api.Provide;

/**
 * Provides arbitrary generators for JAXB object property-based testing.
 */
public class MindmapGenerators {

    @Provide
    public Arbitrary<CalendarMarkings> calendarMarkings() {
        return calendarMarking()
            .list().ofMinSize(0).ofMaxSize(10)
            .map(list -> {
                CalendarMarkings markings = new CalendarMarkings();
                for (CalendarMarking m : list) {
                    markings.addCalendarMarking(m);
                }
                return markings;
            });
    }

    @Provide
    public Arbitrary<CalendarMarking> calendarMarking() {
        Arbitrary<String> names = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(20);
        Arbitrary<String> colors = Arbitraries.of("#ff0000", "#00ff00", "#0000ff", "#ff69b4", "#ffffff");
        Arbitrary<Long> dates = Arbitraries.longs().between(0L, 2000000000000L);
        Arbitrary<String> repeatTypes = Arbitraries.of(
            CalendarMarking.NEVER, CalendarMarking.DAILY, CalendarMarking.WEEKLY,
            CalendarMarking.MONTHLY, CalendarMarking.YEARLY
        );

        return Combinators.combine(names, colors, dates, repeatTypes)
            .as((name, color, startDate, repeatType) -> {
                CalendarMarking m = new CalendarMarking();
                m.setName(name);
                m.setColor(color);
                m.setStartDate(startDate);
                m.setRepeatType(repeatType);
                return m;
            });
    }

    @Provide
    public Arbitrary<Pattern> patterns() {
        Arbitrary<String> names = Arbitraries.strings().alpha().ofMinLength(1).ofMaxLength(30);
        return names.map(name -> {
            Pattern p = new Pattern();
            p.setName(name);
            return p;
        });
    }

    @Provide
    public Arbitrary<Patterns> patternsList() {
        return patterns()
            .list().ofMinSize(1).ofMaxSize(8)
            .map(list -> {
                Patterns ps = new Patterns();
                for (Pattern p : list) {
                    ps.addChoice(p);
                }
                return ps;
            });
    }

    @Provide
    public Arbitrary<CompoundAction> compoundActions() {
        return Arbitraries.integers().between(1, 5).flatMap(size -> {
            Arbitrary<String> nodes = Arbitraries.strings().alpha().ofMinLength(3).ofMaxLength(10);
            return nodes.list().ofSize(size).map(nodeNames -> {
                CompoundAction compound = new CompoundAction();
                for (String nodeName : nodeNames) {
                    BoldNodeAction action = new BoldNodeAction();
                    action.setNode(nodeName);
                    action.setBold(true);
                    compound.addChoice(action);
                }
                return compound;
            });
        });
    }

    @Provide
    public Arbitrary<NormalWindowConfigurationStorage> normalWindowConfigs() {
        Arbitrary<Integer> coords = Arbitraries.integers().between(0, 3000);
        Arbitrary<Integer> sizes = Arbitraries.integers().between(100, 2000);

        return Combinators.combine(coords, coords, sizes, sizes)
            .as((x, y, w, h) -> {
                NormalWindowConfigurationStorage wcs = new NormalWindowConfigurationStorage();
                wcs.setX(x);
                wcs.setY(y);
                wcs.setWidth(w);
                wcs.setHeight(h);
                return wcs;
            });
    }

    @Provide
    public Arbitrary<String> specialStrings() {
        return Arbitraries.strings()
            .withCharRange(' ', '~')
            .ofMinLength(1)
            .ofMaxLength(100)
            .map(s -> s + "<>&\"'");
    }
}
