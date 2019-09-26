from dynabuffers.ast import FieldType
from dynabuffers.ast.AbstractAST import AbstractAST, ByteBuffer


class ClassTypeOptions:
    def __init__(self, name, fields:[FieldType], primary, deprecated):
        self.name = name
        self.fields = fields
        self.primary = primary
        self.deprecated = deprecated


class ClassType(AbstractAST):

    def __init__(self, options: ClassTypeOptions):
        self.options = options

    def size(self, value, registry):
        byName = lambda x: (x.options.name in value) or x.options.defaultVal is not None
        mapper = lambda x: x.size(value[x.options.name] if x.options.name in value else x.options.defaultVal, registry)

        return sum(map(mapper, filter(byName, self.options.fields)))

    def serialize(self, value, buffer: ByteBuffer, registry):
        if self.options.deprecated:
            registry.addNotification("deprecated class " + self.options.name + " used")

        for element in list(filter(lambda x: (x.options.name in value) or x.options.defaultVal is not None, self.options.fields)):
            element.serialize(value[element.options.name] if element.options.name in value else element.options.defaultVal, buffer, registry)

    def deserialize(self, buffer, registry):
        map = {}
        for field in self.options.fields:
            map[field.options.name] = field.deserialize(buffer, registry)

        return map
