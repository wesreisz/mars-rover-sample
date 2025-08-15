# Feature 13: Interactive User Prompt for Input Format

## Story
**As a** user running the Mars Rover application interactively  
**I want** to see clear instructions on the expected input format  
**So that** I know exactly what data to provide without having to consult documentation

## Description
When the Mars Rover application is run interactively (not with piped input), display a helpful prompt that explains the expected input format with examples. This improves user experience by providing immediate guidance on how to structure the mission data.

The prompt should only appear when:
- Running in interactive mode (attached to a console)
- Reading from STDIN directly (not piped or redirected input)
- Before waiting for user input

## Acceptance Criteria

### Scenario 1 - Interactive Mode Shows Prompt
**Given** I run the application interactively without piped input  
**When** I execute `java -jar mars-rovers-all.jar`  
**Then** I should see a prompt explaining the input format  
**And** the prompt should include examples  
**And** the application should wait for my input  
**And** when I provide valid input, the output should be prefixed with "Rover(s) final position is: "

### Scenario 2 - Piped Input Suppresses Prompt
**Given** I have mission data in a file or pipe  
**When** I execute `echo "5 5..." | java -jar mars-rovers-all.jar`  
**Then** the prompt should NOT be displayed  
**And** the application should process the input normally  
**And** the output should still be prefixed with "Rover(s) final position is: "

### Scenario 3 - Error Handling Still Works
**Given** I provide invalid command line arguments  
**When** I execute `java -jar mars-rovers-all.jar --invalid-flag`  
**Then** the usage error message should be displayed  
**And** no input prompt should appear

## Implementation Details

### Prompt Content
```
Please input your plateau and rover commands using:
Line 1: Plateau upper-right coordinates (e.g., '5 5')
For each rover:
  Line N: Starting position (e.g., '1 2 N')
  Line N+1: Instructions (e.g., 'LMLMLMLMM')
Press Ctrl+D (EOF) when finished.
```

### Output Format
All successful mission results should be prefixed with:
```
Rover(s) final position is: 
1 3 N
5 1 E
```

### Technical Notes
- Use `System.console() != null` to detect interactive mode
- Output prompt to STDERR to avoid interfering with mission results on STDOUT
- Maintain backward compatibility with existing CLI behavior
- Ensure tests continue to pass without modification

## Definition of Done
- ✅ Interactive mode displays helpful prompt
- ✅ Piped input works without prompt interference  
- ✅ Command line error handling unchanged
- ✅ All existing tests pass
- ✅ No lint errors introduced
- ✅ JAR can be rebuilt and deployed successfully

## Related Issues
This feature enhances the user experience identified in the original requirement: "If invalid input is entered at the commandline, output on the commandline an example of the expected input." While that requirement was already fulfilled by the existing CLI error handling for invalid flags, this feature extends the concept to provide proactive guidance for valid interactive usage.
