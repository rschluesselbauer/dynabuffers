from dynabuffers.api.IAnnotation import IAnnotation
from dynabuffers.ast.structural import Value


class GreaterEquals(IAnnotation):

    def __init__(self, args:[Value]):
        self.args = args
        if len(args) != 1:
            raise ValueError("too much annotation values")
        if not str(args[0].options.value).isnumeric():
            raise ValueError(str(args[0].options.value) + " is not a number")

    def validate(self, fieldName, value):
        if float(value) < float(self.args[0].options.value):
            raise ValueError("(" + str(fieldName) + ":" + str(value) + ") is lower than " + str(self.args[0].options.value))
