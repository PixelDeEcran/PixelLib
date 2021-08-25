package fr.pixeldeecran.pipilib.command;

public abstract class PSubCommand<T extends PCommand> extends PCommand {

    private T parent;

    @SuppressWarnings("unchecked")
    public void setParent(PCommand parent) {
        this.parent = (T) parent;
    }

    @Override
    public String getMainCommandName() {
        return this.getParent().getMainCommandName();
    }

    @Override
    public String getFullName() {
        return this.getParent().getFullName() + " " + this.getName();
    }

    @Override
    public String getFullUsage() { // TODO : Fix this, surely not working for subcommand of subcommand
        String[] parentUsage = this.getParent() instanceof PSubCommand ?
            this.getParent().getFullUsage().split(" ") :
            this.getParent().getCommandInfo().subCommandUsage().split(" ");
        int subCommandIndex = this.getParent().getCommandInfo().subCommandIndex();

        StringBuilder finalUsage = new StringBuilder();

        finalUsage.append(this.getMainCommandName()).append(" ");

        for (int i = 0; i < subCommandIndex; i++) {
            finalUsage.append(parentUsage[i]).append(" ");
        }

        finalUsage.append(this.getName()).append(" ");
        finalUsage.append(this.getUsage());

        if (finalUsage.charAt(finalUsage.length() - 1) == ' ') {
            return finalUsage.substring(0, finalUsage.length() - 1);
        } else {
            return finalUsage.toString();
        }
    }

    public T getParent() {
        return parent;
    }
}
