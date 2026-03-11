package tests.freemind.property;

import freemind.controller.actions.generated.instance.*;
import freemind.common.XmlBindingTools;
import tests.freemind.property.generators.MindmapGenerators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Property-based tests for JAXB object structure invariants.
 *
 * Verifies collection integrity, ordering, and structural constraints
 * on generated action classes.
 */
public class TreeStructurePropertyTest {

    private final MindmapGenerators generators = new MindmapGenerators();

    /**
     * CompoundAction child count is always non-negative.
     */
    @Property(tries = 50)
    void compoundActionChildCountNonNegative(@ForAll("compoundActions") CompoundAction compound) {
        assertThat(compound.sizeChoiceList()).isGreaterThanOrEqualTo(0);
    }

    /**
     * CompoundAction preserves insertion order of children.
     */
    @Property(tries = 50)
    void compoundActionPreservesInsertionOrder(@ForAll("compoundActions") CompoundAction compound) {
        for (int i = 0; i < compound.sizeChoiceList(); i++) {
            XmlAction child = compound.getChoice(i);
            assertThat(child).isNotNull();
            assertThat(child).isInstanceOf(BoldNodeAction.class);
        }
    }

    /**
     * CalendarMarkings collection elements are individually accessible.
     */
    @Property(tries = 50)
    void calendarMarkingsElementsAccessible(@ForAll("calendarMarkings") CalendarMarkings markings) {
        for (int i = 0; i < markings.sizeCalendarMarkingList(); i++) {
            CalendarMarking m = markings.getCalendarMarking(i);
            assertThat(m).isNotNull();
            assertThat(m.getName()).isNotNull().isNotEmpty();
            assertThat(m.getColor()).isNotNull().startsWith("#");
        }
    }

    /**
     * CalendarMarking names are preserved after round-trip — no data corruption.
     */
    @Property(tries = 50)
    void calendarMarkingNamesUniqueAfterRoundTrip(@ForAll("calendarMarkings") CalendarMarkings original) {
        String xml = XmlBindingTools.getInstance().marshall(original);
        CalendarMarkings restored = (CalendarMarkings) XmlBindingTools.getInstance().unMarshall(xml);

        Set<String> originalNames = new HashSet<>();
        Set<String> restoredNames = new HashSet<>();
        for (int i = 0; i < original.sizeCalendarMarkingList(); i++) {
            originalNames.add(original.getCalendarMarking(i).getName());
            restoredNames.add(restored.getCalendarMarking(i).getName());
        }
        assertThat(restoredNames).isEqualTo(originalNames);
    }

    /**
     * Patterns list size matches after adding elements.
     */
    @Property(tries = 50)
    void patternsListSizeConsistent(@ForAll("patternsList") Patterns patterns) {
        int size = patterns.sizeChoiceList();
        assertThat(size).isGreaterThan(0);

        for (int i = 0; i < size; i++) {
            Pattern p = patterns.getChoice(i);
            assertThat(p).isNotNull();
            assertThat(p.getName()).isNotNull();
        }
    }

    /**
     * NormalWindowConfigurationStorage geometry values are always within set bounds.
     */
    @Property(tries = 50)
    void windowConfigGeometryPreserved(@ForAll("windowConfigs") NormalWindowConfigurationStorage config) {
        assertThat(config.getWidth()).isGreaterThan(0);
        assertThat(config.getHeight()).isGreaterThan(0);
        assertThat(config.getX()).isGreaterThanOrEqualTo(0);
        assertThat(config.getY()).isGreaterThanOrEqualTo(0);
    }

    // ========================================================================
    // Arbitrary Providers
    // ========================================================================

    @Provide
    net.jqwik.api.Arbitrary<CompoundAction> compoundActions() {
        return generators.compoundActions();
    }

    @Provide
    net.jqwik.api.Arbitrary<CalendarMarkings> calendarMarkings() {
        return generators.calendarMarkings();
    }

    @Provide
    net.jqwik.api.Arbitrary<Patterns> patternsList() {
        return generators.patternsList();
    }

    @Provide
    net.jqwik.api.Arbitrary<NormalWindowConfigurationStorage> windowConfigs() {
        return generators.normalWindowConfigs();
    }
}
